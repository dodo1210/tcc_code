import librosa
from pydub import AudioSegment

for n in range(5000):
    y, sr = librosa.load("/home/douglas/Documentos/tcc_code/corte/cortes/file"+str(n)+".wav")
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr,n_chroma=12, n_fft=4096)
    chroma_cq = librosa.feature.chroma_cqt(y=y, sr=sr)

    sound = AudioSegment.from_wav("/home/douglas/Documentos/tcc_code/corte/cortes/file"+str(n)+".wav")

    count = 0
    pos = 0

    do = 0
    dos = 0
    re = 0
    res = 0
    mi = 0
    fa = 0
    fas = 0
    sol = 0
    sols = 0
    la = 0
    las = 0
    si = 0

    maximo = float(sound.max_dBFS)
    

    if maximo >  -12.8/2:
        for i in range(len(chroma_cq)):
            for l in range(len(chroma_cq[i])):
                #print(i,chroma_cq[i][l])
                if chroma_cq[i][l]==1:
                    if i == 0:
                        do+=1
                    if i == 1:
                        dos+=1
                    if i == 2:
                        re+=1
                    if i == 3:
                        res+=1
                    if i == 4:
                        mi+=1
                    if i == 5:
                        fa+=1
                    if i == 6:
                        fas+=1
                    if i == 7:
                        sol+=1
                    if i == 8:
                        sols+=1
                    if i == 9:
                        la+=1
                    if i == 10:
                        las+=1
                    if i == 11:
                        si+=1   
        print(n,maximo,do,dos,re,res,mi,fa,fas,sol,sols,la,las,si)              
    elif maximo > -12.8/2-3:
        for i in range(len(chroma_cq)):
            for l in range(len(chroma_cq[i])):
                #print(i,chroma_cq[i][l])
                if chroma_cq[i][l]==1:
                    if i == 0:
                        do+=1
                    if i == 1:
                        dos+=1
                    if i == 2:
                        re+=1
                    if i == 3:
                        res+=1
                    if i == 4:
                        mi+=1
                    if i == 5:
                        fa+=1
                    if i == 6:
                        fas+=1
                    if i == 7:
                        sol+=1
                    if i == 8:
                        sols+=1
                    if i == 9:
                        la+=1
                    if i == 10:
                        las+=1
                    if i == 11:
                        si+=1   
        if do==6 or dos==5 or dos==6 or re==5 or  re==6 or res==5 or res==6 or mi==5 or  mi==6 or fa==5 or  fa==6 or fas==5 or fas==6 or sol==5 or sol==6 or sols==5 or sols ==6 or la==5 or  la==6 or las==5 or las==6 or si==6:
            print(n,maximo,do,dos,re,res,mi,fa,fas,sol,sols,la,las,si)  
    