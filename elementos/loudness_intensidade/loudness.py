from pydub import AudioSegment

arq = open('felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR
    sound = AudioSegment.from_wav('felizes_30\\'+music)
    peak_amplitude = loudness = sound.dBFS
    print(peak_amplitude)