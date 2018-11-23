# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
import matplotlib.pyplot as plt


# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music)
    # 3. Run the default beat tracker
    tempo, beat_frames = librosa.beat.beat_track(y=y, sr=sr)
    print("MUSIC"+music)
    print('Estimated tempo: {:.2f} beats per minute'.format(tempo))
