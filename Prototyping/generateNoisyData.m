% Script to generate Test Data for the DTMF Decoder
% TODO: summary of how the data is produced and variales being
% changed/tested
tic
numFiles = input('Enter the number of files to be generated for each power range: ');
Fs = input('Enter the sampling Frequency: ');
folderName = 'Noisy Test Data';
disp(strcat('Your files will be located in the folder "',folderName,'" inside the directory of this script'));

if (~exist(folderName,'dir'))
    mkdir(folderName);
%     mkdir(folderName,'/0.5dB');
%     mkdir(folderName,'/1dB');
%     mkdir(folderName,'/2dB');
%     mkdir(folderName,'/5dB');
    mkdir(folderName,'/11dB');
    mkdir(folderName,'/14dB');
%     mkdir(folderName,'/10dB');
    mkdir(folderName,'/16dB');
    mkdir(folderName,'/18dB');
    mkdir(folderName,'/19dB');
    mkdir(folderName,'/25dB');
    mkdir(folderName,'/30dB');
%     mkdir(folderName,'/50dB');
%     mkdir(folderName,'/60dB');

else
%     if (~exist(strcat(folderName,'/0.5dB'),'dir'))
%         mkdir(folderName,'/0.5dB');
%     end
%     if (~exist(strcat(folderName,'/1dB'),'dir'))
%         mkdir(folderName,'/1dB');
%     end
%     if (~exist(strcat(folderName,'/2dB'),'dir'))
%         mkdir(folderName,'/2dB');
%     end
%     if (~exist(strcat(folderName,'/5dB'),'dir'))
%         mkdir(folderName,'/5dB');
%     end
    if (~exist(strcat(folderName,'/11dB'),'dir'))
        mkdir(folderName,'/11dB');
    end
    if (~exist(strcat(folderName,'/14dB'),'dir'))
        mkdir(folderName,'/14dB');
    end
    if (~exist(strcat(folderName,'/16dB'),'dir'))
        mkdir(folderName,'/16dB');
    end
    if (~exist(strcat(folderName,'/18dB'),'dir'))
        mkdir(folderName,'/18dB');
    end
    if (~exist(strcat(folderName,'/19dB'),'dir'))
        mkdir(folderName,'/19dB');
    end
    if (~exist(strcat(folderName,'/25dB'),'dir'))
        mkdir(folderName,'/25dB');
    end
    if (~exist(strcat(folderName,'/30dB'),'dir'))
        mkdir(folderName,'/30dB');
    end
%     if (~exist(strcat(folderName,'/40dB'),'dir'))
%         mkdir(folderName,'/30dB');
%     end
%     if (~exist(strcat(folderName,'/50dB'),'dir'))
%         mkdir(folderName,'/30dB');
%     end
%     if (~exist(strcat(folderName,'/60dB'),'dir'))
%         mkdir(folderName,'/30dB');
%     end
end

% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,0.5);
%     name = strcat(folderName,'/0.5dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 0.5dB');
%     end
% end
% 
% disp('Done with the first SNR');
% 
% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,1);
%     name = strcat(folderName,'/1dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 1dB');
%     end
% end
% 
% disp('Done with the second SNR');
% 
% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,2);
%     name = strcat(folderName,'/2dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 2dB');
%     end
% end
% 
% disp('Done with the third SNR');

% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,5);
%     name = strcat(folderName,'/5dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 5dB');
%     end
% end


% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,5);
%     name = strcat(folderName,'/5dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 5dB');
%     end
% end

parfor count = 1:numFiles
    [x, chars] = randSeq(randomInt(5,50),Fs,1);
    y = awgn(x,11);
    name = strcat(folderName,'/11dB/',chars,'.wav');
    if (exist(name,'file'))
    %    count = count - 1;
        continue;
    else
        audiowrite(name,y,Fs);
    end
    if (count == floor(numFiles/2))
        disp('Halfway through 11dB');
    end
end

parfor count = 1:numFiles
    [x, chars] = randSeq(randomInt(5,50),Fs,1);
    y = awgn(x,14);
    name = strcat(folderName,'/14dB/',chars,'.wav');
    if (exist(name,'file'))
    %    count = count - 1;
        continue;
    else
        audiowrite(name,y,Fs);
    end
    if (count == floor(numFiles/2))
        disp('Halfway through 14dB');
    end
end

parfor count = 1:numFiles
    [x, chars] = randSeq(randomInt(5,50),Fs,1);
    y = awgn(x,16);
    name = strcat(folderName,'/16dB/',chars,'.wav');
    if (exist(name,'file'))
    %    count = count - 1;
        continue;
    else
        audiowrite(name,y,Fs);
    end
    if (count == floor(numFiles/2))
        disp('Halfway through 16dB');
    end
end

parfor count = 1:numFiles
    [x, chars] = randSeq(randomInt(5,50),Fs,1);
    y = awgn(x,18);
    name = strcat(folderName,'/18dB/',chars,'.wav');
    if (exist(name,'file'))
    %    count = count - 1;
        continue;
    else
        audiowrite(name,y,Fs);
    end
    if (count == floor(numFiles/2))
        disp('Halfway through 18dB');
    end
end

parfor count = 1:numFiles
    [x, chars] = randSeq(randomInt(5,50),Fs,1);
    y = awgn(x,19);
    name = strcat(folderName,'/19dB/',chars,'.wav');
    if (exist(name,'file'))
    %    count = count - 1;
        continue;
    else
        audiowrite(name,y,Fs);
    end
    if (count == floor(numFiles/2))
        disp('Halfway through 19dB');
    end
end

parfor count = 1:numFiles
    [x, chars] = randSeq(randomInt(5,50),Fs,1);
    y = awgn(x,25);
    name = strcat(folderName,'/25dB/',chars,'.wav');
    if (exist(name,'file'))
    %    count = count - 1;
        continue;
    else
        audiowrite(name,y,Fs);
    end
    if (count == floor(numFiles/2))
        disp('Halfway through 25dB');
    end
end

parfor count = 1:numFiles
    [x, chars] = randSeq(randomInt(5,50),Fs,1);
    y = awgn(x,30);
    name = strcat(folderName,'/30dB/',chars,'.wav');
    if (exist(name,'file'))
    %    count = count - 1;
        continue;
    else
        audiowrite(name,y,Fs);
    end
    if (count == floor(numFiles/2))
        disp('Halfway through 30dB');
    end
end

disp('Done with the sixth power range');

% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,30);
%     name = strcat(folderName,'/30dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 30dB');
%     end
% end
% 
% 
% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,40);
%     name = strcat(folderName,'/40dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 40dB');
%     end
% end


% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,50);
%     name = strcat(folderName,'/50dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 50dB');
%     end
% end
% 
% 
% parfor count = 1:numFiles
%     [x, chars] = randSeq(randomInt(5,50),Fs,1);
%     y = awgn(x,60);
%     name = strcat(folderName,'/60dB/',chars,'.wav');
%     if (exist(name,'file'))
%     %    count = count - 1;
%         continue;
%     else
%         audiowrite(name,y,Fs);
%     end
%     if (count == floor(numFiles/2))
%         disp('Halfway through 60dB');
%     end
% end

% disp('Done with the seventh power range');
disp(strcat('Time: ',num2str(toc),'seconds'));
