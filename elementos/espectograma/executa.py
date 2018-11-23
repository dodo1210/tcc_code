import scipy
import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

arq = open("isso.txt",'a+')
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #carregamento dos arquivos
    sample_rate, X = scipy.io.wavfile.read("/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/"+music)
    frequencies, times, spectrogram = signal.spectrogram(X, sample_rate)
    sin_data = np.sin(spectrogram)
    print(str(spectrogram.mean()))



'''print(len(sin_data),len(sin_data[1]),sin_data[11111][1],X.min(),X.max(),X.mean())
for i in range(len(sin_data)):
    for l in range(len(sin_data[i])):
        print(sin_data[i][l])
plt.specgram(spectrogram, Fs=sample_rate, xextent=(0,120))
plt.ylabel('Frequency [Hz]')
plt.xlabel('Time [sec]')
plt.show()'''

