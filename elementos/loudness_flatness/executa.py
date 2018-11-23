# -*- coding: utf-8 -*-
# Beat tracking example
import essentia
import essentia.standard as es
import essentia.streaming as ess

# 1. Get the file path to the included audio example

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
#captura da musica
arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    music, erro = l.split("\n",1)
    # Loading audio file
    import IPython
    IPython.display.Audio('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music)
    features, features_frames = es.MusicExtractor(lowlevelStats=['mean', 'stdev'],
                                              rhythmStats=['mean', 'stdev'],
                                              tonalStats=['mean', 'stdev'])('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music)

    # See all feature names in the pool in a sorted order
    print("Filename:", features['lowlevel.barkbands_flatness_db.mean'])
    