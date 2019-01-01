# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
import numpy as np


# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
1
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/tristes/'+music,sr=44100)
    S = np.abs(librosa.core.stft(y, n_fft=2048, hop_length=512, win_length=1024, window='hann'))
    a, phase = librosa.magphase(S)
    print(music,a.mean())
    lista.append(a.mean())
    
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    arq.write(music+","+str(lista[count])+"\n")
    count+=1



   