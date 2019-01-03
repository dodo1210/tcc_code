arq = open('/home/douglas/Documentos/tcc_code/programas/processamento_de_audio_java/jAudio/smoothness_tr.txt','r')
captura = arq.readlines()
arq.close()


arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','r')
musics = arq.readlines()
arq.close()


count=0
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultados_tristes.csv','w')
for m in musics:
    music, erro = m.split("\n",1)
    cap, erro = captura[count].split("\n",1)
    arq.write(music+","+str(cap)+"\n")
    count+=1