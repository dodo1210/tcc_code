import librosa
import numpy as np
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
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music,sr=44100)
    y_harmonic = librosa.effects.harmonic(y)
    S = np.abs(librosa.core.stft(y_harmonic, n_fft=1024, hop_length=512, win_length=1024, window='hann'))
    print(S.mean())
