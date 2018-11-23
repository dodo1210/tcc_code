from timbral_models import timbral_brightness

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR

    fname = '/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music
    # calculate brightness
    roug = timbral_brightness(fname) 
    print(roug)