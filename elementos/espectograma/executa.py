import scipy
import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #carregamento dos arquivos
    sample_rate, X = scipy.io.wavfile.read("/home/douglas/Música/musicas/wav/tristes/"+music)
    frequencies, times, spectrogram = signal.spectrogram(X, 44100)
    sin_data = np.sin(spectrogram)
    print(music,str(spectrogram.mean()))
    lista.append(spectrogram.mean())

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


'''print(len(sin_data),len(sin_data[1]),sin_data[11111][1],X.min(),X.max(),X.mean())
for i in range(len(sin_data)):
    for l in range(len(sin_data[i])):
        print(sin_data[i][l])
plt.specgram(spectrogram, Fs=sample_rate, xextent=(0,120))
plt.ylabel('Frequency [Hz]')
plt.xlabel('Time [sec]')
plt.show()'''

