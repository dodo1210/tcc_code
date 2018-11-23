#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
import librosa
#abertura do arquivo
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

arq = open("intensidade_musicas_felizes.txt",'a+')
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music,sr=44100)
    sound = AudioSegment.from_wav("/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/"+music)
    cent = librosa.feature.rmse(y=y, frame_length=1024, hop_length=512)
    a = sound.max_dBFS/cent.mean()
    print(a)


