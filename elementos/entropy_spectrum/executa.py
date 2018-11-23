import scipy
import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
from scipy.stats import entropy
import librosa
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
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music,sr=44100)
    '''sample_rate, X = scipy.io.wavfile.read("/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/"+music)'''
    S = np.abs(librosa.core.stft(y, n_fft=1024, hop_length=512, win_length=1024, window='hann'))
    frequencies, times, spectrogram = signal.spectrogram(S, 44100)
    print(entropy(spectrogram).mean())
    
