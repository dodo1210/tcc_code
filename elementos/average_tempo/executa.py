# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
import matplotlib.pyplot as plt
from pydub import AudioSegment


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
    tempo, beats = librosa.beat.beat_track(y, sr=sr)
    beat_times = librosa.frames_to_time(beats, sr=sr)
    i=0
    tempo = 0 
    soma=0
    for i in range(len(beat_times)-1):
        sound = AudioSegment.from_wav('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music)
        parte1 = sound[beat_times[i]*1000:beat_times[i+1]*1000]
        parte1.export('parte1.wav',format="wav")
        y, sr = librosa.load('parte1.wav')
        temp, beat_frames = librosa.beat.beat_track(y=y, sr=sr)
        tempo+=temp
    print(music,tempo/len(beat_times))
