# -*- coding: utf-8 -*-
import mido
import matplotlib.pyplot as plt
import csv

#abertura do arquivo
arq = open('/home/douglas/Documentos/tcc_code/musicas/midi/tristes.txt','r')
lines = arq.readlines()

#contadores dos intervalos
c_unison=0
c_segunda_maior=0
c_segunda_menor=0
terca_maior=0
c_terca_maior=0
terca_menor=0
c_terca_menor=0
c_quarta_maior=0
c_quarta_menor=0
c_quinta_maior=0
c_quinta_menor=0
c_sexta_maior=0
c_sexta_menor=0
c_setima_maior=0
c_setima_menor=0

c = 0

#iniciar teste
print("BEGIN")
print("canal")
channel=input()
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    mid = mido.MidiFile('/home/douglas/Documentos/tcc_code/musicas/midi/tristes/'+music)
    arq_notes = open("notas/tristes/notes "+music+".txt",'w')
    print("MUSIC"+music)
    notes = []
    #extração dos valores
    for mid_extract in mid.play():
        arq_notes.write("EXTRACT: "+str(mid_extract)+"\n")
        if "channel="+channel in str(mid_extract) and "note_on" in str(mid_extract):
            error,str_aux = str(mid_extract).split("note=")
            note, error = str_aux.split(" v")
            notes.append(float(note))
    arq_notes.close()
    #contagem dos intervalos
    print("INTERVALS"+music)
    count = 0
    arq = open("intervalo/tristes/"+music+'.txt','w') 
    for note in notes:
        if len(notes) == count+1:
            break
        if note-notes[count+1]==0:
            c_unison=c_unison+1
            arq.write("unison\n")
        
        if note-notes[count+1]==1 or note-notes[count+1]==-1:
            arq.write("segunda menor\n")
            c_segunda_menor=c_segunda_menor+1
        
        if note-notes[count+1]==2 or note-notes[count+1]==-2:
            arq.write("segunda maior\n")
            c_segunda_maior=c_segunda_maior+1
        
        if note-notes[count+1]==3 or note-notes[count+1]==-3:
            arq.write("terceira menor\n")
            c_terca_menor = c_terca_menor+1
            terca_menor = terca_menor+1
        
        if note-notes[count+1]==4 or note-notes[count+1]==-4:
            arq.write("terceira maior\n")
            c_terca_maior = c_terca_maior+1
            terca_maior = terca_maior+1
        
        if note-notes[count+1]==5 or note-notes[count+1]==-5:
            arq.write("quarta justa\n")
            c_quarta_menor=c_quarta_menor+1
            
        if note-notes[count+1]==6 or note-notes[count+1]==-6:
            arq.write("quarta aumentada\n")
            c_quarta_maior=c_quarta_maior+1
            
        if note-notes[count+1]==7 or note-notes[count+1]==-7:
            arq.write("quinta justa\n")
            c_quinta_menor=c_quinta_menor+1
            
        if note-notes[count+1]==8 or note-notes[count+1]==-8:
            arq.write("quinta aumentada\n")
            c_quinta_maior=c_quinta_maior+1
            
        if note-notes[count+1]==9 or note-notes[count+1]==-9:
            arq.write("sexta maior\n")
            c_sexta_maior=c_sexta_maior+1
        
        if note-notes[count+1]==10 or note-notes[count+1]==-10:
            arq.write("sétima menor\n")
            c_setima_menor=c_setima_menor+1
            
        if note-notes[count+1]==11 or note-notes[count+1]==-11:
            arq.write("sétima maior\n")
            c_setima_maior=c_setima_maior+1
            
        count = count+1
    #gráfico de barras de cada música
    '''fig = plt.figure()

    x = [2]
    y = [c_terca_maior]
    
    x2 = [1]
    y2 = [c_terca_menor]
    
    # Criando um gráfico
    plt.bar(x, y, label = 'terça maior', color = 'r')
    plt.bar(x2, y2, label = 'terça menor', color = 'y')
    plt.title(music)
    
    fig.savefig("/home/douglas/Documentos/tcc_code/resultados/"+music+".png")'''

    print("unison"+str(c_unison))     
    print("2m"+str(c_segunda_menor))
    print("2M"+str(c_segunda_maior))
    print("3m"+str(c_terca_menor))
    print("3M"+str(c_terca_maior))
    print("4M"+str(c_quarta_menor))
    print("4m"+str(c_quarta_maior))
    print("5M"+str(c_quinta_menor))
    print("5m"+str(c_quinta_maior))
    print("6M"+str(c_sexta_maior))
    print("7m"+str(c_setima_menor))
    print("7M"+str(c_setima_maior))

    #zerar dos contagem
    c_unison=0
    c_segunda_maior=0
    c_segunda_menor=0
    c_terca_maior=0
    c_terca_menor=0
    c_quarta_maior=0
    c_quarta_menor=0
    c_quinta_maior=0
    c_quinta_menor=0
    c_sexta_maior=0
    c_sexta_menor=0
    c_setima_maior=0
    c_setima_menor=0



#gráfico de pizza com a contagem de todas as terças
arq.close()

# Criando um gráfico

fig = plt.figure()

labels = ['terca_maior','terca_menor']
titulos = [terca_maior,terca_menor]
cores = ['lightblue', 'green']
explode = (0.1, 0)  # somente explode primeiro pedaço
total = sum(titulos)
plt.pie(titulos, explode=explode, labels=labels, colors=cores, autopct=lambda p: '{:.0f}'.format(p * total / 100), shadow=True, startangle=90)

# Determina que as proporções sejam iguais ('equal') de modo a desenhar o círculo
plt.axis('equal') 
plt.title("Quantidade de Terças nas músicas")
fig.figure("musicas_tristes.png")

'''x = [2]
y = [terca_maior]

x2 = [1]
y2 = [terca_menor]

# Criando um gráfico
plt.bar(x, y, label = 'terça maior', color = 'r')
plt.bar(x2, y2, label = 'terça menor', color = 'y')
plt.title("total das musicas")

plt.show()'''

print("THE END.")