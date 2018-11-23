# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa

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
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr,n_chroma=12, n_fft=1024,hop_length=512)
    chroma_cq = librosa.feature.chroma_cqt(y=y, sr=sr)
    print(chroma_cq[0].mean(),chroma_cq[1].mean(),chroma_cq[2].mean(),chroma_cq[3].mean(),chroma_cq[4].mean(),chroma_cq[5].mean(),chroma_cq[6].mean(),chroma_cq[7].mean(),chroma_cq[8].mean(),chroma_cq[9].mean(),chroma_cq[10].mean(),chroma_cq[11].mean())