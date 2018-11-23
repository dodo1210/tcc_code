#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
import librosa

#captura da musica
sound = AudioSegment.from_wav("/home/douglas/Documentos/tcc_code/bpm/the-BPM-detector-python-master/bpm_detection/Adele - Someone Like You.wav")
sound = sound+sound+sound
sound.export("/home/douglas/Documentos/a1.wav", format="wav")

''y, sr = librosa.load('/home/douglas/Documentos/a1.wav')
tempo, beat_frames = librosa.beat.beat_track(y=y, sr=sr)
#print("MUSIC"+music)
print('Estimated tempo: {:.2f} beats per minute'.format(tempo))

cut = (60000/float(tempo))*16

sound = AudioSegment.from_wav("/home/douglas/Documentos/a1.wav")
print(sound.max_dBFS)

cont = 0 

peak_amplitude = sound.max #pega a amplitude da musica
loudness = sound.dBFS #so peguei ai
new_sample_rate = int(sound.frame_rate)
print(new_sample_rate)

#definição da música toda para corte 
for i in range(len(sound)):
	# divisão da musica
	if(i >= (len(sound)/cut)*(cont+1)):
		j = (len(sound)/cut) * cont 
		# corte da música 
		parte1 = sound[j:i]
		#exportação da musica
		parte1.export("cortes/file"+str(cont)+".wav", format="wav")
		cont = cont+1
''
