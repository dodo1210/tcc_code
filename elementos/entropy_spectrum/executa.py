import scipy
import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
from scipy.stats import entropy
import librosa

arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0

for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #carregamento dos arquivos
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/tristes/'+music,sr=44100)
    S = np.abs(librosa.core.stft(y, n_fft=2048, hop_length=512, win_length=1024, window='hann'))
    frequencies, times, spectrogram = signal.spectrogram(S, 44100)
    print(music,entropy(spectrogram).mean())
    lista.append(entropy(spectrogram).mean())
    
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=1