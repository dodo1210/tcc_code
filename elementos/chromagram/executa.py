# -*- coding: utf-8 -*-
# Beat tracking example
from __future__ import print_function
import librosa

# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Música/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/felizes/'+music,sr=44100)
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr,n_chroma=12, n_fft=2048,hop_length=512)
    chroma_cq = librosa.feature.chroma_cqt(y=y, sr=sr)
    print(chroma_cq[0].mean(),chroma_cq[1].mean(),chroma_cq[2].mean(),chroma_cq[3].mean(),chroma_cq[4].mean(),chroma_cq[5].mean(),chroma_cq[6].mean(),chroma_cq[7].mean(),chroma_cq[8].mean(),chroma_cq[9].mean(),chroma_cq[10].mean(),chroma_cq[11].mean())
    lista.append(chroma_cq[0].mean())
    lista.append(chroma_cq[1].mean())
    lista.append(chroma_cq[2].mean())
    lista.append(chroma_cq[3].mean())
    lista.append(chroma_cq[4].mean())
    lista.append(chroma_cq[5].mean())
    lista.append(chroma_cq[6].mean())
    lista.append(chroma_cq[7].mean())
    lista.append(chroma_cq[8].mean())
    lista.append(chroma_cq[9].mean())
    lista.append(chroma_cq[10].mean())
    lista.append(chroma_cq[11].mean())
    print(music)


arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=1
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=2
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=3
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=4
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12
arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=5
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=6
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=7
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=8
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=9
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12

arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=10
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12


arq.close()

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()

count=11
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=12
