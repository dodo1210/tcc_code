#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
import librosa
#abertura do arquivo
arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/tristes/'+music,sr=44100)
    sound = AudioSegment.from_wav("/home/douglas/Música/musicas/wav/tristes/"+music)
    cent = librosa.feature.rmse(y=y, frame_length=2048, hop_length=512)
    a = sound.max_dBFS/cent.mean()
    print(music,a)
    lista.append(a)

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    arq.write(music+","+str(lista[count])+"\n")
    count+=1
    


