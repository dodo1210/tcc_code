import os

arq = open('/home/douglas/Documentos/tcc_code/musicas/midi/Adele_someone_like_you.txt','r')
stri = arq.readlines()

cont = 0
anterior = 0
atual = 0
for oi in stri:
	if cont>0:
		'''if float(oi)-atual==0:
		    print("unison")
		if float(oi)-atual==1 or float(oi)-atual==-1:
		    print("segunda menor")
		if float(oi)-atual==2 or float(oi)-atual==-2:
		    print("segunda maior")'''
		if float(oi)-atual==3 or float(oi)-atual==-3:
		    print("terceira menor")
		if float(oi)-atual==4 or float(oi)-atual==-4:
		    print("terceira maior")
		'''if float(oi)-atual==5 or float(oi)-atual==-5:
		    print("quarta justa")
		if float(oi)-atual==6 or float(oi)-atual==-6:
		    print("quarta aumentada")
		if float(oi)-atual==7 or float(oi)-atual==-7:
		    print("quinta justa")
		if float(oi)-atual==8 or float(oi)-atual==-8:
		    print("quinta aumentada")
		if float(oi)-atual==9 or float(oi)-atual==-9:
		    print("sexta maior")
		if float(oi)-atual==10 or float(oi)-atual==-10:
		    print("sétima menor")
		if float(oi)-atual==11 or float(oi)-atual==-11:
		    print("sétima maior")'''
	cont = cont+1
	atual = float(oi)
	

arq.close()