from __future__ import print_function
import librosa
import matplotlib.pyplot as plt

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music,sr=44100)
    y = librosa.feature.zero_crossing_rate(y,frame_length=1024, hop_length=512)
    print(y.mean())
