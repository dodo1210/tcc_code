# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
import matplotlib.pyplot as plt
import numpy as np

# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Música/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/felizes/'+music,sr=44100)
    D = np.abs(librosa.core.stft(y, n_fft=2048, hop_length=512, win_length=1024, window='hann'))
    print(music,D.mean())
    lista.append(D.mean())

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    arq.write(music+","+str(lista[count])+"\n")
    count+=1


