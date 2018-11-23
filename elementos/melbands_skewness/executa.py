import scipy
import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
from scipy.stats import skew
import librosa
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
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music,sr=44100)
    S = librosa.feature.melspectrogram(y=y, sr=sr, n_mels=13,fmax=8000)
    a = librosa.feature.mfcc(S=librosa.power_to_db(S))
    print(skew(a).mean())
    
    
