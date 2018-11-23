import numpy as np
#import matplotlib.pyplot as plt
from scipy import signal
import scipy.io.wavfile
from scipy.stats import linregress
import librosa

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #carregamento dos arquivos
    y, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/'+music,sr=44100)
    y_harmonic = librosa.effects.harmonic(y)
    S = np.abs(librosa.core.stft(y, n_fft=1024, hop_length=512, win_length=1024, window='hann'))
    a = librosa.feature.mfcc(S=librosa.power_to_db(S),n_mfcc=2)
    # 3. Run the default beat tracker
    slope, intercept, r_value, p_value, std_err = linregress(a[0],a[1])
    print(slope)

    
