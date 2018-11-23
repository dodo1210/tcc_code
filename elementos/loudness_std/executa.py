#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
import numpy as np
import math
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
    sound = AudioSegment.from_wav("/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/"+music)
    a = math.sqrt(math.pow(float(sound.max_dBFS),2))
    print(a)
    

