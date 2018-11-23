import vamp
import librosa
data, rate = librosa.load("/home/douglas/Documentos/tcc_code/musicas/wav/tristes/31 - Adele - Someone Like You.mp3.wav")
chroma = vamp.collect(data, rate, "nnls-chroma:nnls-chroma")
print(chroma)