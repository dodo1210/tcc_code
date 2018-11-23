import music21
from music21 import *

score = music21.converter.parse('/home/douglas/Documentos/tcc_code/ritmo/003 - Journey_-_Don__t_Stop_Believin__.mid')
print(score.ratioString)
