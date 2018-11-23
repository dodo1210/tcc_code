import scipy
import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
from scipy.stats import kurtosis
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
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
    sample_rate, X = scipy.io.wavfile.read("/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/"+music)
    frequencies, times, spectrogram = signal.spectrogram(X, 44100)
    print(kurtosis(spectrogram).mean())
    
