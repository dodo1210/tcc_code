import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
from scipy.stats import linregress
import librosa

arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #carregamento dos arquivos
    y, sr = librosa.load('/home/douglas/Música/musicas/wav/tristes/'+music,sr=44100)
    y_harmonic = librosa.effects.harmonic(y)
    S = np.abs(librosa.core.stft(y, n_fft=1024, hop_length=512, win_length=1024, window='hann'))
    a = librosa.feature.mfcc(S=librosa.power_to_db(S),n_mfcc=2)
    # 3. Run the default beat tracker
    slope, intercept, r_value, p_value, std_err = linregress(a[0],a[1])
    print(music,slope)
    lista.append(slope)

arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    arq.write(music+","+str(lista[count])+"\n")
    count+=1

    
