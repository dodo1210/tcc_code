# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
from pydub import AudioSegment
import numpy as np

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
    oenv = librosa.onset.onset_strength(y=y, sr=sr)
    # Detect events without backtracking
    onset_raw = librosa.onset.onset_detect(onset_envelope=oenv,backtrack=False)
    # Backtrack the events using the onset envelope
    onset_bt = librosa.onset.onset_backtrack(onset_raw, oenv)
    maximum = librosa.frames_to_time(onset_bt, sr=sr)
    minimum = librosa.frames_to_time(onset_raw, sr=sr)
    i=0
    soma=0
    for i in range(len(onset_bt)) :
        soma+=minimum[i]-maximum[i]
    print(soma/len(onset_bt))

	#onset_frames = librosa.onset.onset_detect(y=y, sr=sr)