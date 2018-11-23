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
arq = open('tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('tristes_30\\'+music)
    tempo, beats = librosa.beat.beat_track(y, sr=sr)
    beat_times = librosa.frames_to_time(beats, sr=sr)
    i=0
    max = 0
    for i in range(len(beat_times)-1):
        sound = AudioSegment.from_wav('tristes_30\\'+music)
        parte1 = sound[beat_times[i]*1000:beat_times[i+1]*1000]
        parte1.export('parte1.wav',format="wav")
        y, sr = librosa.load('parte1.wav',sr=44100)
        y = librosa.onset.onset_strength(y=y, sr=sr)
        if max<y.max():
        	max = y.max()
    print(max)

	#onset_frames = librosa.onset.onset_detect(y=y, sr=sr)