from mido import Message, MidiFile, MidiTrack

mid = MidiFile()
track = MidiTrack()
mid.tracks.append(track)

track.append(Message('program_change', program=12, time=0))
  
arq = open('/home/douglas/Documentos/tcc_code/criar_midi/novo.txt','r')
lines = arq.readlines()
arq.close()
atual = 0
for l in lines:
    music, erro = l.split("\n",1)
    if int(music) != atual:
        track.append(Message('note_on', note=int(music)+48, velocity=64, time=32))
        track.append(Message('note_off', note=int(music)+48, velocity=127, time=32))
    atual = int(music)

mid.save('new_song.mid')