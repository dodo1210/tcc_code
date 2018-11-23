import librosa
import numpy as np

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music,sr=44100)
    S = np.abs(librosa.core.stft(y, n_fft=1024, hop_length=512, win_length=1024, window='hann'))
    a = librosa.power_to_db(S**2)
    i=0
    max = a[0][0]-a[0][1]
    for i in range(len(a)):
        for l in range((len(a[i])-1)):
            if max<(a[i][l]-a[i][l+1]):
                max = a[i][l]-a[i][l+1]
    print(max)

