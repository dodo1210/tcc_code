import librosa
from pydub import AudioSegment

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

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

for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    y, sr = librosa.load("/home/douglas/Documentos/tcc_code/musicas/wav/tristes/"+str(music),sr=44100)
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr,n_chroma=12, n_fft=1024, hop_length=512)
    for i in range(len(chroma_stft)):
        for l in range(len(chroma_stft[i])):
            #print(i,chroma_cq[i][l])
            if chroma_stft[i][l]==1:
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
    print(100/len(chroma_stft[0])*do,100/len(chroma_stft[0])*dos,100/len(chroma_stft[0])*re,100/len(chroma_stft[0])*res,100/len(chroma_stft[0])*mi,100/len(chroma_stft[0])*fa,100/len(chroma_stft[0])*fas,100/len(chroma_stft[0])*sol,100/len(chroma_stft[0])*sols,100/len(chroma_stft[0])*la,100/len(chroma_stft[0])*las,100/len(chroma_stft[0])*si)
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
