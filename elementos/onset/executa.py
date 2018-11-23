# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
import matplotlib.pyplot as plt


# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music)
    # 3. Run the default beat tracker
    onset_frames = librosa.onset.onset_detect(y=y, sr=sr)
    print(len(onset_frames))