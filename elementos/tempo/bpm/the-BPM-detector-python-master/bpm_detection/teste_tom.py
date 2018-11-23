from music21 import *

import music21
score = music21.converter.parse('Ring01.wav.mid')
key = score.analyze('key')
f = note.Note(key.tonic.name)
tom = key.tonic.name
teste = pitch.Pitch(tom)
print(tom)
print(teste.midi)
#x = music21.chord.Chord(antes+" "+depois)
#print(x.pitchedCommonName)
print("--------------------------------------------------------------------")
