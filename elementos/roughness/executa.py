from timbral_models import timbral_roughness

# generic file location

arq = open('/home/douglas/Música/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()

lista = []

count=0
for l in lines:
    fname = '/home/douglas/Documentos/tcc_code/musicas/wav/felizes_30'+music
    # calculate brightness
    brightness = timbral_roughness(fname) 
    print(brightness)