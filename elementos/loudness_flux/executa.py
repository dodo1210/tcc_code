import librosa
import numpy as np

arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/tristes/'+music,sr=44100)
    S = np.abs(librosa.core.stft(y, n_fft=2048, hop_length=512, win_length=1024, window='hann'))
    a = librosa.power_to_db(S**2)
    i=0
    max = a[0][0]-a[0][1]
    for i in range(len(a)):
        for l in range((len(a[i])-1)):
            if max<(a[i][l]-a[i][l+1]):
                max = a[i][l]-a[i][l+1]
    lista.append(max)
    print(music,max)

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    arq.write(music+","+str(lista[count])+"\n")
    count+=1