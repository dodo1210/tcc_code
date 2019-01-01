from pydub import AudioSegment

arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    sound = AudioSegment.from_wav('/home/douglas/Música/musicas/wav/tristes/'+music)
    peak_amplitude = loudness = sound.dBFS
    print(music,peak_amplitude)
    lista.append(peak_amplitude)

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    arq.write(music+","+str(lista[count])+"\n")
    count+=1

