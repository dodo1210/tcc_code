#!/usr/bin/env
# -*- coding: utf-8 -*-
from requests_html import HTMLSession
session = HTMLSession()
import re
import csv
#Use - no lugar de espços. Ex: someone-like-you
#coldplay/the-scientist

musica = "the-scientist"
artista = "coldplay" 

r = session.get('https://www.cifraclub.com.br/'+artista+"/"+musica)
arq =  open('felizes/'+musica+'.txt','w')
arq.write(r.text)
arq.close()

arq = open('felizes/'+musica+'.txt','r')
lines = arq.readlines()

arqui = open('acordes.txt','w')

#elimina linhas que não contem acortes 
for l in lines:
    if l.find('fácil') > -1:
        break
    if l.find('<b>') != -1 and (len(l)<300 and (len(l)<150 or l.find('Intro')!=-1)):
        arqui.write(l)


arq.close()
arqui.close()

arq = open('acordes.txt','r')
lines = arq.readlines()

print((artista+" - "+musica)+", ,"+str(len(re.findall('<b>C</b>', str(lines))))+","+str(len(re.findall('<b>C#</b>',str(lines))))+","+	str(len(re.findall('<b>Cm</b>',str(lines))))+","+	str(len(re.findall('<b>C#m</b>',str(lines))))+","+	str(len(re.findall('<b>D</b>',str(lines))))+","+	str(len(re.findall('<b>D#</b>',str(lines))))+","+	str(len(re.findall('<b>Dm</b>',str(lines))))+","+	str(len(re.findall('<b>D#m</b>',str(lines))))+","+	str(len(re.findall('<b>E</b>',str(lines))))+","+	str(len(re.findall('<b>Eb</b>',str(lines))))+","+	str(len(re.findall('<b>Em</b>',str(lines))))+","+	str(len(re.findall('<b>Ebm</b>',str(lines))))+","+	str(len(re.findall('<b>F</b>',str(lines))))+","+	str(len(re.findall('<b>F#</b>',str(lines))))+","+	str(len(re.findall('<b>Fm</b>',str(lines))))+","+	str(len(re.findall('<b>F#m</b>',str(lines))))+","+	str(len(re.findall('<b>G</b>',str(lines))))+","+	str(len(re.findall('<b>G#</b>',str(lines))))+","+	str(len(re.findall('<b>Gm</b>',str(lines))))+","+	str(len(re.findall('<b>G#m</b>',str(lines))))+","+	str(len(re.findall('<b>A</b>',str(lines))))+","+	str(len(re.findall('<b>A#</b>',str(lines))))+","+	str(len(re.findall('<b>Am</b>',str(lines))))+","+	str(len(re.findall('<b>A#m</b>',str(lines))))+","+	str(len(re.findall('<b>B</b>',str(lines))))+","+	str(len(re.findall('<b>Bb</b>',str(lines))))+","+	str(len(re.findall('<b>Bm</b>',str(lines))))+","+str(len(re.findall('<b>Bbm</b>',str(lines)))))


arq.close()

#str(len(re.findall('<b>Cb</b>',str(lines))))+","+	len(re.findall('<b>Cm</b>',str(lines))),	len(re.findall('<b>Cbm</b>',str(lines))),	len(re.findall('<b>D</b>',str(lines))),	len(re.findall('<b>Db</b>',str(lines))),	len(re.findall('<b>Dm</b>',str(lines))),	len(re.findall('<b>Dbm</b>',str(lines))),	len(re.findall('<b>E</b>',str(lines))),	len(re.findall('<b>Eb</b>',str(lines))),	len(re.findall('<b>Em</b>',str(lines))),	len(re.findall('<b>Ebm</b>',str(lines))),	len(re.findall('<b>F</b>',str(lines))),	len(re.findall('<b>Fb</b>',str(lines))),	len(re.findall('<b>Fm</b>',str(lines))),	len(re.findall('<b>Fbm</b>',str(lines))),	len(re.findall('<b>G</b>',str(lines))),	len(re.findall('<b>Gb</b>',str(lines))),	len(re.findall('<b>Gm</b>',str(lines))),	len(re.findall('<b>Gbm</b>',str(lines))),	len(re.findall('<b>A</b>',str(lines))),	len(re.findall('<b>Ab</b>',str(lines))),	len(re.findall('<b>Am</b>',str(lines))),	len(re.findall('<b>Abm</b>',str(lines))),	len(re.findall('<b>B</b>',str(lines))),	len(re.findall('<b>Bb</b>',str(lines))),	len(re.findall('<b>Bm</b>',str(lines))),	len(re.findall('<b>Bbm</b>',str(lines)))