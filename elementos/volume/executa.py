#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
#abertura do arquivo
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

arq = open("intensidade_musicas_tristes.txt",'a+')
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    sound = AudioSegment.from_wav("/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/"+music)
    print(str(sound.max_dBFS))

