# first, we need to import our essentia module. It is aptly named 'essentia'!
import essentia
import essentia.standard as es
import essentia.streaming as ess

# as there are 2 operating modes in essentia which have the same algorithms,
# these latter are dispatched into 2 submodules:


# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    # Loading audio file
    features, features_frames = es.MusicExtractor(lowlevelStats=['mean', 'stdev'],
                                              rhythmStats=['mean', 'stdev'],
                                              tonalStats=['mean', 'stdev'])('/home/douglas/Música/musicas/wav/tristes/'+music)

    # See all feature names in the pool in a sorted order
    for i in range(36):
        print(music, features['tonal.hpcp.mean'][i])
        lista.append(features['tonal.hpcp.mean'][i])

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+","+str(lista[count])+
    ","+str(lista[count+1])+
    ","+str(lista[count+2])+
    ","+str(lista[count+3])+
    ","+str(lista[count+4])+
    ","+str(lista[count+5])+
    ","+str(lista[count+6])+
    ","+str(lista[count+7])+
    ","+str(lista[count+8])+
    ","+str(lista[count+9])+
    ","+str(lista[count+10])+
    ","+str(lista[count+11])+
    ","+str(lista[count+12])+
    ","+str(lista[count+13])+
    ","+str(lista[count+14])+
    ","+str(lista[count+15])+
    ","+str(lista[count+16])+
    ","+str(lista[count+17])+
    ","+str(lista[count+18])+
    ","+str(lista[count+19])+
    ","+str(lista[count+20])+
    ","+str(lista[count+21])+
    ","+str(lista[count+22])+
    ","+str(lista[count+23])+
    ","+str(lista[count+24])+
    ","+str(lista[count+25])+
    ","+str(lista[count+26])+
    ","+str(lista[count+27])+
    ","+str(lista[count+28])+
    ","+str(lista[count+29])+
    ","+str(lista[count+30])+
    ","+str(lista[count+31])+"\n")
    count+=32