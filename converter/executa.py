#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
'''
#captura da musica
arq = open('/home/douglas/Documentos/tcc_code/musicas/midi/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    print("MUSIC"+music)'''
sound = AudioSegment.from_mp3('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/019 - OneRepublic - Secrets.mp3')
sound.export("/home/douglas/Documentos/tcc_code/musicas/wav/tristes/019 - OneRepublic - Secrets", format="wav")
