# first, we need to import our essentia module. It is aptly named 'essentia'!
import essentia
import essentia.standard as es

# as there are 2 operating modes in essentia which have the same algorithms,
# these latter are dispatched into 2 submodules:


# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    # Loading audio file
    import IPython
    IPython.display.Audio('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music)
    features, features_frames = es.MusicExtractor(lowlevelStats=['mean', 'stdev'],
                                              rhythmStats=['mean', 'stdev'],
                                              tonalStats=['mean', 'stdev'])('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music)

    # See all feature names in the pool in a sorted order
    print(features['lowlevel.dynamic_complexity'])
