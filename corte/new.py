#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
import librosa

#captura da musica
#abertura do arquivo
arq = open('/media/douglas/Douglas/Musicas_tristes/Bad_Days/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    print(music)
    sound = AudioSegment.from_mp3("/media/douglas/Douglas/Musicas_tristes/Bad_Days/"+music)
    cut = sound[0:30*1000]
    #exportação da musica
    cut.export("/home/douglas/Documentos/tcc_code/musicas/wav/tristes/"+music+".wav", format="wav")

