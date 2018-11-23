#!/usr/bin/env
# -*- coding: utf-8 -*-
from pydub import AudioSegment
import librosa

#captura da musica
sound = AudioSegment.from_("/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30/060 - No Doubt - Don_t Speak.mp3.wav")

parte1 = sound[1*1000:30*1000]
#exportação da musica
parte1.export("1.wav", format="wav")

