from requests_html import HTMLSession
session = HTMLSession()

#Use - no lugar de espços. Ex: someone-like-you
# 

musica = "the-scientist"
artista = "coldplay"

r = session.get('https://www.cifraclub.com.br/'+artista+'/'+musica+'/')
arq =  open('felizes/'+musica+'.txt','w')
arq.write(r.text)
arq.close()

arq = open('felizes/'+musica+'.txt','r')
lines = arq.readlines()

arqui = open('acordes.txt','w')

#elimina linhas que não contem acortes 
for l in lines:
    if l.find('fácil') > -1:
        break
    if l.find('<b>') != -1 and (len(l)<300 and (len(l)<150 or l.find('Intro')!=-1)):
        arqui.write(l)


arq.close()
arqui.close()

arq = open('acordes.txt','r')
lines = arq.readlines()

get = ""
acordes = []
a = 0
tudo = ""

for l in lines:
    for i in range(len(l)-1):
        if l[i+1] == '/':
            i+=1
        if l.find('interm')!=-1 or l.find('iniciante')!=-1:
            break
        if l[i] == '>':
            a = 100
        if l[i+1] == '<':
            acordes.append(get)
            get = ""
            a=0
        if a == 100:
            tudo = tudo+l[i+1]
            get=get+l[i+1]

maiores = 0
menores = 0

arq = open('felizes/acordes_'+musica+'.txt','w')

tudo = ""
for i in acordes:
    tudo+=i


import re
b = re.sub('[^a-zA-G]', ',\n', tudo)
arq.write(b)

arq.close()

arq = open('felizes/acordes_'+musica+'.txt','r')
lines = arq.readlines()

count = 0
erro=0

for l in lines:
    if l.find('m') >= 0 :
        menores+=1
    if l.find('m')>=0 and len(l)==3:
        erro = erro+1
    elif l==',\n' or l=='span,\n' or l=='sspan,\n' or l=='class,\n' or l=='tablatura,\n' or l=='ntro,\n' or l=='b,\n':
        maiores=maiores
    else:
        maiores=maiores+1
    
print("quantidade de valores encontrados",maiores-menores,menores)
total = (maiores-menores)+menores
print("quantidade de valores encontrados",(100/total)*(maiores-menores),(100/total)*menores)
#37 13