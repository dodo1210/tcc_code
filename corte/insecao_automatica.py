arq = open('/home/douglas/Documentos/tcc_code/resultado/resultado.csv')
string = arq.readlines()
arq.close()
arq = open('/home/douglas/Documentos/tcc_code/resultado/resultado.csv','w')
count = 0
for s in string:
    oi, erro = s.split("\n",1)
    count +=1
    if count<=500:
        arq.write(oi+",0\n")
    else:
        arq.write(oi+",1\n")
arq.close()