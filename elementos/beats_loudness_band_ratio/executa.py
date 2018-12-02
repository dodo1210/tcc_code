# first, we need to import our essentia module. It is aptly named 'essentia'!
import essentia
import essentia.standard as es

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
    print("Filename:", features['rhythm.beats_loudness_band_ratio.mean'])
    lista.append(features['rhythm.beats_loudness_band_ratio.mean'])

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()

count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+","+str(lista[count])+"\n")
    arq.write(music+","+str(lista[count])+"\n")
    count+=1
    