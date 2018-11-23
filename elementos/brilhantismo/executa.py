import librosa

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music)
    cent = librosa.feature.spectral_centroid(y=y, sr=sr)
    print(cent.mean())