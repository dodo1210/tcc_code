from __future__ import print_function
import librosa
import numpy as np

#captura audio
y, sr = librosa.load('/home/douglas/Música/wav/tristes/31 - Adele - Someone Like You.mp3.wav')

'''
tempo da música
print(y,sr)

tempo, beats = librosa.beat.beat_track(y=y, sr=sr)

print(tempo,beats)

print(librosa.frames_to_time(beats, sr=sr))'''

'''
deixa áudio 2 vezes mais rápido
y_fast = librosa.effects.time_stretch(y, 2.0)
print(y_fast)'''

S = np.abs(librosa.stft(y))
print(S)
comps, acts = librosa.decompose.decompose(S, n_components=8)
print(comps,acts)