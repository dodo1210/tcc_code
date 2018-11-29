#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
import librosa

#captura da musica
#abertura do arquivo
arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    print(music)
    sound = AudioSegment.from_mp3("/home/douglas/Música/musicas/wav/tristes/"+music)
    cut = sound[0:30*1000]
    #exportação da musica
    cut.export(music+"wav", format="wav")

