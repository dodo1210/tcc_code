import librosa

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music,sr=44100)
    y_harmonic = librosa.effects.harmonic(y)
    i=1
    max = y_harmonic[0]-y_harmonic[1]
    for i in range(len(y_harmonic)-1):
        if max<(y_harmonic[i]-y_harmonic[i+1]):
            max = y_harmonic[i]-y_harmonic[i+1]
    print(max)

