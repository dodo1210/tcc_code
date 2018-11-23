# -*- coding: utf-8 -*-
# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
from pydub import AudioSegment
from scipy import ndimage
import numpy as np


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
    y,sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music,sr=44100)
    S = np.abs(librosa.core.stft(y, n_fft=1024, hop_length=512, win_length=1024, window='hann'))
    a = librosa.power_to_db(S**2)
    centroid = ndimage.measurements.center_of_mass(a)
    print(a[int(centroid[0])][int(centroid[1])])