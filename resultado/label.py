arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','r')
musics = arq.readlines()
arq.close()


arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_felizes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    print(music+",0")
    arq.write(music+",0\n")
