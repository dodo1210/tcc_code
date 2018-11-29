import scipy
import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
# -*- coding: utf-8 -*-
# Beat tracking example
import librosa
import matplotlib.pyplot as plt
from pydub import AudioSegment


# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/tristes/'+music)
    tempo, beats = librosa.beat.beat_track(y, sr=sr)
    beat_times = librosa.frames_to_time(beats, sr=sr)
    i=0
    tempo = 0 
    soma=0
    for i in range(len(beat_times)-1):
        sound = AudioSegment.from_wav('/home/douglas/Música/musicas/wav/tristes/'+music)
        parte1 = sound[beat_times[i]*1000:beat_times[i+1]*1000]
        parte1.export('parte1.wav',format="wav")
        sample_rate, X = scipy.io.wavfile.read("parte1.wav")
        frequencies, times, spectrogram = signal.spectrogram(X, 44100)
        sin_data = np.sin(spectrogram) 
        soma+=sin_data.mean()
    print(soma)
    lista.append(soma)
        
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()

count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=1
