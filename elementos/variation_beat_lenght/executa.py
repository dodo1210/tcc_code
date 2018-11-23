# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
import matplotlib.pyplot as plt
from pydub import AudioSegment
import numpy


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
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music,sr=44100)
    tempo, beats = librosa.beat.beat_track(y, sr=sr)
    beat_times = librosa.frames_to_time(beats, sr=sr)
    i=0
    soma = 0
    for i in range(len(beat_times)-2):
        temp, beat_frames = librosa.beat.beat_track(y=y, sr=sr)
        beat_times_diff = numpy.diff(beat_times)
        soma=+(60/tempo)*(beat_times_diff[i+1]-beat_times_diff[i])
    print("soma: ",soma)
