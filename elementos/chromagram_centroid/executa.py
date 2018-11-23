# -*- coding: utf-8 -*-
# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa
from pydub import AudioSegment
from scipy import ndimage


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
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/'+music,sr=44100)
    chroma = librosa.feature.chroma_stft(y=y, sr=sr, n_fft=1024, hop_length=512)
    centroid = ndimage.measurements.center_of_mass(chroma)
    print(chroma[int(centroid[0])][int(centroid[1])])