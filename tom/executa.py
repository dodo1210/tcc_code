from music21 import *
import music21

#abertura do arquivo 
arq = open('/home/douglas/Documentos/tcc_code/musicas/midi/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)

#iniciar teste
print("BEGIN")

arq = open("isso.txt",'a+')
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    score = music21.converter.parse('/home/douglas/Documentos/tcc_code/musicas/midi/tristes/'+music)
    tom = score.analyze('key')
    frequencia = note.Note(tom.tonic.name)
    #print(str(key.pitchToSharps(str(tom.tonic.name),str(tom.mode))))
    print(music+" "+str(tom.tonic.name)+", "+str(tom.mode)+", "+str(frequencia.pitch.frequency))


