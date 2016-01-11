package src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to decode DTMF signals within a wav file.
 * 
 * @author Tinotenda Chemvura
 *
 */
public class DTMFUtil {

	public static ArrayList<String> noisyTemp = new ArrayList<String>();
	
	private final double CUT_OFF_POWER = 0.001;
	private final double CUT_OFF_POWER_NOISE_RATIO = 0.68;
	private final double FRAME_DURATION = 0.038;
	private boolean decoded;
	private String seq[];
	private WavFile wavFile;
	private int frameSize;
	private int totalFramesRead = 0;
	// freqs = {697, 770, 852, 941, 1209, 1336, 1477, 1633};
	private static int[] fbin = {
			687, 692, 697, 702, 707,				// 697
			758, 764, 770, 776, 782, 						// 770
			839, 852, 865, 						// 852
			927, 941, 955, 						// 941
			1191, 1209, 1227, 					// 1209
			1316, 1336, 1356, 					// 1336
			1455, 1477, 1499, 					// 1477
			1609, 1633, 1647, 1657 }; 			// 1633
	
	/**
	 * Create DTMFUtil object for a wav file given the WavFile object
	 * @param data waveFile object to be processed.
	 */
	public DTMFUtil(WavFile data) {
		this.wavFile = data;
		setFrameSize();
		this.decoded = false;
		seq = new String[2];
		this.seq[0] = "";
		this.seq[1] = "";
	}
	
	/**
	 * Create DTMFUtil object for a wav file given the filename
	 * @param filename Filename of the wav file to be processed
	 * @throws IOException
	 * @throws WavFileException
	 */
	public DTMFUtil(String filename) throws IOException, WavFileException{
		this.wavFile = FileUtil.readWavFileBuffer(filename);
		setFrameSize();
		this.decoded = false;
		seq = new String[2];
		this.seq[0] = "";
		this.seq[1] = "";
	}
	
	/**
	 * Create DTMFUtil object for a wav file given the file object
	 * @param file File object for the wav file
	 * @throws IOException
	 * @throws WavFileException
	 */
	public DTMFUtil(File file) throws IOException, WavFileException{
		this.wavFile = FileUtil.readWavFileBuffer(file);
		setFrameSize();
		this.decoded = false;
		seq = new String[2];
		this.seq[0] = "";
		this.seq[1] = "";
	}


	/**
	 * Method to set the frame size for the decoding process
	 */
	private void setFrameSize() {
		this.frameSize = (int) Math.floor(FRAME_DURATION * wavFile.getSampleRate());
	}

	/**
	 * Method to get the highest DTMF freq within the tolerance range and use
	 * that magnitude to represet the corresponsing DTMF freq
	 * 
	 * @param frame
	 *            Frame with 274 magnitudes to be processed
	 * @return an array with 8 magnitudes. Each representing the magnitude of
	 *         each frequency
	 */
	private static double[] filterFrame(double[] frame) {
		double[] out = new double[8];

		out[0] = DecoderUtil.max(Arrays.copyOfRange(frame, 0, 3));
		out[1] = DecoderUtil.max(Arrays.copyOfRange(frame, 3, 6));
		out[2] = DecoderUtil.max(Arrays.copyOfRange(frame, 6, 9));
		out[3] = DecoderUtil.max(Arrays.copyOfRange(frame, 9, 12));
		out[4] = DecoderUtil.max(Arrays.copyOfRange(frame, 12, 15));
		out[5] = DecoderUtil.max(Arrays.copyOfRange(frame, 15, 18));
		out[6] = DecoderUtil.max(Arrays.copyOfRange(frame, 18, 21));
		out[7] = DecoderUtil.max(Arrays.copyOfRange(frame, 21, 25));

		return out;
	}

	/**
	 * Method returns the DTMF sequence
	 * 
	 * @return char array with the keys represented in the file
	 * @throws DTMFDecoderException
	 *             Throws excepion when the file has not been decoded yet.
	 */
	public String[] getSequence() throws DTMFDecoderException {
		if (!decoded)
			throw new DTMFDecoderException("File has not been decoded yet. Please run the method decode() first.");
		return seq;
	}

	/**
	 * Method to generate a frequency spectrum of the frame
	 * 
	 * @param frame
	 *            Frame to be transformed
	 * @param Fs
	 *            Sampling Frequency
	 * @return an Array showing the realtive powers of the DTMF frequencies
	 */
	private static double[] transformFrame(double[] frame, int Fs) {
		double[] temp = new double[25];
		double[] out = new double[8];
		Goertzel g = new Goertzel(Fs, frame.length, fbin);
		// 1. transform the frames using goertzel algorithm
		// 2. get the highest DTMF freq within the tolerance range and use that
		// magnitude to represet the corresponsing DTMF free
		temp = g.calcFreqWeight(frame);
		out = filterFrame(temp);
		return out;
	}

	/**
	 * Method to detect whether a frame is too noisy for detection
	 * 
	 * @param dft_data
	 *            Frequency spectrum magnitudes for the DTMF frequencies
	 * @return true is noisy or false if it is acceptable
	 */
	private boolean isNoisy(double[] dft_data) {
		// sum the powers of all frequencies = sum
		// find ratio of the (sum of two highest peaks) : sum
		double[] temp = Arrays.copyOf(dft_data, dft_data.length);
		Arrays.sort(temp);
		double one = temp[temp.length - 1];
		double two = temp[temp.length - 2];
		double sum = DecoderUtil.sumArray(temp);
		double ratio = (one + two) / sum;
		noisyTemp.add(Double.toString(ratio));
		return ratio < CUT_OFF_POWER_NOISE_RATIO;
		// return
		// ((temp[temp.length-1]+temp[temp.length-2])/DecoderUtil.sumArray(temp))
		// < CUT_OFF_POWER_NOISE_RATIO;
	}

	/**
	 * Method to decode a frame given the frequency spectrum information of the
	 * frame
	 * 
	 * @param dft_data
	 *            Frequency spectrum information showing the relative magnitudes
	 *            of the power of each DTMF frequency
	 * @return DTMF charatcter represented by the frame
	 * @throws DTMFDecoderException
	 */
	private static char getRawChar(double[] dft_data) throws DTMFDecoderException {
		char out = 0;
		int low, hi;
		double[] lower = Arrays.copyOfRange(dft_data, 0, 4);
		double[] higher = Arrays.copyOfRange(dft_data, 4, 8);

		low = DecoderUtil.maxIndex(lower);
		hi = DecoderUtil.maxIndex(higher);

		if (low == 0) { // low = 697
			if (hi == 0) { // High = 1209
				out = '1';
			} else if (hi == 1) { // high = 1336
				out = '2';
			} else if (hi == 2) { // high = 1477
				out = '3';
			} else if (hi == 3) { // high = 1633
				out = 'A';
			} else
				throw new DTMFDecoderException("Something went terribly wrong!");

		} else if (low == 1) { // low = 770
			if (hi == 0) { // high = 1209
				out = '4';
			} else if (hi == 1) { // high = 1336
				out = '5';
			} else if (hi == 2) { // high = 1477
				out = '6';
			} else if (hi == 3) { // high = 1633
				out = 'B';
			} else
				throw new DTMFDecoderException("Something went terribly wrong!");

		} else if (low == 2) { // low = 852
			if (hi == 0) { // high = 1209
				out = '7';
			} else if (hi == 1) { // high = 1336
				out = '8';
			} else if (hi == 2) { // high = 1477
				out = '9';
			} else if (hi == 3) { // high = 1633
				out = 'C';
			} else
				throw new DTMFDecoderException("Something went terribly wrong!");

		} else if (low == 3) { // low = 941
			if (hi == 0) { // high = 1209
				out = '*';
			} else if (hi == 1) { // high = 1336
				out = '0';
			} else if (hi == 2) { // high = 1477
				out = '#';
			} else if (hi == 3) { // high = 1633
				out = 'D';
			} else
				throw new DTMFDecoderException("Something went terribly wrong!");
		} else
			throw new DTMFDecoderException("Something went terribly wrong!");
		return out;
	}

	/**
	 * Method to decode the next frame in a buffer of a mono channeled wav file
	 * 
	 * @return the decoded DTMF character
	 * @throws IOException
	 * @throws WavFileException
	 * @throws DTMFDecoderException
	 */
	private char decodeNextFrame1() throws IOException, WavFileException, DTMFDecoderException {
		

		double[] buffer1 = new double[(int) Math.floor(frameSize / 3)]; // read
																		// 370
																		// samples
																		// at a
																		// time
		double[] tempBuffer11 = new double[(int) Math.floor(frameSize / 3)];
		double[] tempBuffer21 = new double[(int) Math.floor(frameSize / 3)];

		int framesRead = wavFile.readFrames(buffer1, (int) Math.floor(frameSize / 3));
		totalFramesRead+= framesRead;
		if (framesRead < frameSize / 3)
			throw new DTMFDecoderException("Out of frames");

		double[] frame = DecoderUtil.concatenateAll(tempBuffer21, tempBuffer11, buffer1);
		tempBuffer21 = tempBuffer11;
		tempBuffer11 = buffer1;

		char out = 'T';
		// check if the power of the signal is high enough to be accepted.
		double power = DecoderUtil.signalPower(frame);
		if (power < CUT_OFF_POWER)
			return '_';
		// transform frame
		double[] dft_data = DTMFUtil.transformFrame(frame, (int) wavFile.getSampleRate());

		// check if the frame has too much noise
		if (isNoisy(dft_data))
			return '_';

		try {
			out = DTMFUtil.getRawChar(dft_data);
		} catch (DTMFDecoderException e) {
			e.printStackTrace();
			return 'Q';
		}
		return out;
	}

	/**
	 * Method to decode the wav file.
	 * 
	 * @return String representation of the sequence of DTMF tones represented
	 *         in the wav file
	 * @throws IOException
	 * @throws WavFileException
	 */
	private void decodeMono() throws IOException, WavFileException {
		char prev = '_';
		char prev2 = '_';
		String seq2 = "";
		do {

			char curr;
			try {
				curr = decodeNextFrame1();
			} catch (DTMFDecoderException e) {
				break;
			}
//			System.out.print(curr);
			if (curr != '_') {
				if (curr == prev) { // eliminate false positives
					if (curr != prev2) {
						seq2 += curr;
					}
				}
			}
			prev2 = prev;
			prev = curr;
		} while (true);
		seq[0] = seq2;
	}

	/**
	 * Method to decode the wav file.
	 * 
	 * @return String representation of the sequence of DTMF tones represented
	 *         in the wav file
	 * @throws IOException
	 * @throws WavFileException
	 */
	private void decodeStereo() throws IOException, WavFileException {
		char curr[];
		char[] prev = { '_', '_' };
		char[] prev2 = { '_', '_' };
		String[] seq2 = { "", "" };
		do {

			try {
				curr = decodeNextFrame2();
			} catch (DTMFDecoderException e) {
				break;
			}

			// decode channel 1
			if (curr[0] != '_') {
				if (curr[0] == prev[0]) { // eliminate false positives
					if (curr[0] != prev2[0]) {
						seq2[0] += curr[0];
					}
				}
			}
			prev2[0] = prev[0];
			prev[0] = curr[0];

			// decode channel 2
			if (curr[1] != '_') {
				if (curr[1] == prev[1]) { // eliminate false positives
					if (curr[1] != prev2[1]) {
						seq2[1] += curr[1];
					}
				}
			}
			prev2[1] = prev[1];
			prev[1] = curr[1];

		} while (true);
		seq = seq2;
	}

	/**
	 * Method to decode the next frame in a buffer of a stereo wav file
	 * 
	 * @return the decoded DTMF character
	 * @throws IOException
	 * @throws WavFileException
	 * @throws DTMFDecoderException
	 */
	private char[] decodeNextFrame2() throws IOException, WavFileException, DTMFDecoderException {
		double[][] buffer = new double[2][(int) Math.floor(frameSize / 3)]; // read
																			// 370
																			// samples
																			// at
																			// a
																			// time
		double[] tempBuffer11 = new double[(int) Math.floor(frameSize / 3)];
		double[] tempBuffer21 = new double[(int) Math.floor(frameSize / 3)];
		double[] tempBuffer12 = new double[(int) Math.floor(frameSize / 3)];
		double[] tempBuffer22 = new double[(int) Math.floor(frameSize / 3)];
		int framesRead = 0;

		framesRead = wavFile.readFrames(buffer, (int) Math.floor(frameSize / 3));
		if (framesRead < frameSize / 3)
			throw new DTMFDecoderException("Out of frames");

		double[] frame1 = DecoderUtil.concatenateAll(tempBuffer21, tempBuffer11, buffer[0]);
		double[] frame2 = DecoderUtil.concatenateAll(tempBuffer22, tempBuffer12, buffer[1]);
		tempBuffer21 = tempBuffer11;
		tempBuffer11 = buffer[0];

		tempBuffer22 = tempBuffer12;
		tempBuffer12 = buffer[1];

		char[] outArr = { 'T', 'T' };
		// check if the power of the signal is high enough to be accepted.
		if (DecoderUtil.signalPower(frame1) < CUT_OFF_POWER) {
			outArr[0] = '_';
		}
		if (DecoderUtil.signalPower(frame2) < CUT_OFF_POWER) {
			outArr[1] = '_';
		}

		if (outArr[0] == '_' && outArr[1] == '_') {
			return outArr;
		}

		// transform frame
		double[] dft_data1 = DTMFUtil.transformFrame(frame1, (int) wavFile.getSampleRate());
		double[] dft_data2 = DTMFUtil.transformFrame(frame2, (int) wavFile.getSampleRate());

		// check if the frame has too much noise
		if (isNoisy(dft_data1)) {
			outArr[0] = '_';
		}
		if (isNoisy(dft_data2)) {
			outArr[1] = '_';
		}

		if (outArr[0] == '_' && outArr[1] == '_') {
			return outArr;
		}

		try {
			if (outArr[0] != '_') {
				outArr[0] = DTMFUtil.getRawChar(dft_data1);
			}
			if (outArr[1] != '_') {
				outArr[1] = DTMFUtil.getRawChar(dft_data2);
			}
		} catch (DTMFDecoderException e) {
			e.printStackTrace();
			throw new DTMFDecoderException("Something went wrong.");
		}
		return outArr;
	}

	/**
	 * Method to decode the wav file and return the sequence of DTMF tones
	 * represented.
	 * 
	 * @return String array with the sequence of tones for each channel
	 * @throws IOException
	 * @throws WavFileException
	 * @throws DTMFDecoderException
	 */
	public String[] decode() throws IOException, WavFileException, DTMFDecoderException {
		if (decoded) {
			return seq;
		}
		if (wavFile.getNumChannels() == 1) {
			decodeMono();
			decoded = true;
		} else if (wavFile.getNumChannels() == 2) {
			decodeStereo();
			decoded = true;
		} else
			throw new DTMFDecoderException("Can only decode mono and stereo wav files.");
		return seq;
	}
}
