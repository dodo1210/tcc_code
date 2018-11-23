import mido
import music21
from music21 import *

arq = open('/home/douglas/Documentos/tcc_code/musicas/midi/felizes.txt','r')
lines = arq.readlines()

#iniciar teste
print("BEGIN")
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    mid = mido.MidiFile('/home/douglas/Documentos/tcc_code/musicas/midi/felizes/'+music)
    score = music21.converter.parse('/home/douglas/Documentos/tcc_code/musicas/midi/felizes/'+music)
    notes = []
    
    soma=0
    c=0
    cs=0
    d=0
    ds=0
    e=0
    f=0
    fs=0
    g=0
    gs=0
    a=0
    asu=0
    s=0
    b=0

    #extração dos valores
    for mid_extract in mid.play():
        soma+=1
        if str(mid_extract).find('note=0') > -1 or str(mid_extract).find('note=12') > -1 or str(mid_extract).find('note=24') > -1 or str(mid_extract).find('note=36') > -1 or str(mid_extract).find('note=48') > -1 or str(mid_extract).find('note=60') > -1 or str(mid_extract).find('note=72') > -1 or str(mid_extract).find('note=84') > -1 or str(mid_extract).find('note=96') > -1 or str(mid_extract).find('note=108') > -1 or str(mid_extract).find('note=120') > -1:

            c+=1

        if str(mid_extract).find('note=1') > -1 or str(mid_extract).find('note=13') > -1 or str(mid_extract).find('note=25') > -1 or str(mid_extract).find('note=37') > -1 or str(mid_extract).find('note=49') > -1 or str(mid_extract).find('note=61') > -1 or str(mid_extract).find('note=73') > -1 or str(mid_extract).find('note=85') > -1 or str(mid_extract).find('note=97') > -1 or str(mid_extract).find('note=109') > -1 or str(mid_extract).find('note=121') > -1:

            cs+=1

        if str(mid_extract).find('note=2') > -1 or str(mid_extract).find('note=14') > -1 or str(mid_extract).find('note=26') > -1 or str(mid_extract).find('note=38') > -1 or str(mid_extract).find('note=50') > -1 or str(mid_extract).find('note=62') > -1 or str(mid_extract).find('note=74') > -1 or str(mid_extract).find('note=86') > -1 or str(mid_extract).find('note=98') > -1 or str(mid_extract).find('note=110') > -1 or str(mid_extract).find('note=122') > -1:
            
            
            d+=1
            
        if str(mid_extract).find('note=3') > -1 or str(mid_extract).find('note=15') > -1 or str(mid_extract).find('note=27') > -1 or str(mid_extract).find('note=39') > -1 or str(mid_extract).find('note=51') > -1 or str(mid_extract).find('note=63') > -1 or str(mid_extract).find('note=75') > -1 or str(mid_extract).find('note=87') > -1 or str(mid_extract).find('note=99') > -1 or str(mid_extract).find('note=111') > -1 or str(mid_extract).find('note=123') > -1:
             
            ds+=1

        if str(mid_extract).find('note=4') > -1 or str(mid_extract).find('note=16') > -1 or str(mid_extract).find('note=28') > -1 or str(mid_extract).find('note=40') > -1 or str(mid_extract).find('note=52') > -1 or str(mid_extract).find('note=64') > -1 or str(mid_extract).find('note=76') > -1 or str(mid_extract).find('note=88') > -1 or str(mid_extract).find('note=100') > -1 or str(mid_extract).find('note=112') > -1 or str(mid_extract).find('note=124') > -1:

            e+=1

        if str(mid_extract).find('note=5') > -1 or str(mid_extract).find('note=17') > -1 or str(mid_extract).find('note=29') > -1 or str(mid_extract).find('note=41') > -1 or str(mid_extract).find('note=53') > -1 or str(mid_extract).find('note=65') > -1 or str(mid_extract).find('note=77') > -1 or str(mid_extract).find('note=89') > -1 or str(mid_extract).find('note=101') > -1 or str(mid_extract).find('note=113') > -1 or str(mid_extract).find('note=125') > -1:

            f+=1

        if str(mid_extract).find('note=6') > -1 or str(mid_extract).find('note=18') > -1 or str(mid_extract).find('note=30') > -1 or str(mid_extract).find('note=42') > -1 or str(mid_extract).find('note=54') > -1 or str(mid_extract).find('note=66') > -1 or str(mid_extract).find('note=78') > -1 or str(mid_extract).find('note=90') > -1 or str(mid_extract).find('note=102') > -1 or str(mid_extract).find('note=114') > -1 or str(mid_extract).find('note=126') > -1:

            fs+=1

        if str(mid_extract).find('note=7') > -1 or str(mid_extract).find('note=19') > -1 or str(mid_extract).find('note=31') > -1 or str(mid_extract).find('note=43') > -1 or str(mid_extract).find('note=55') > -1 or str(mid_extract).find('note=67') > -1 or str(mid_extract).find('note=79') > -1 or str(mid_extract).find('note=91') > -1 or str(mid_extract).find('note=103') > -1 or str(mid_extract).find('note=115') > -1 or str(mid_extract).find('note=127') > -1:

            g+=1

        if str(mid_extract).find('note=8') > -1 or str(mid_extract).find('note=20') > -1 or str(mid_extract).find('note=32') > -1 or str(mid_extract).find('note=44') > -1 or str(mid_extract).find('note=56') > -1 or str(mid_extract).find('note=68') > -1 or str(mid_extract).find('note=80') > -1 or str(mid_extract).find('note=92') > -1 or str(mid_extract).find('note=104') > -1 or str(mid_extract).find('note=116') > -1 or str(mid_extract).find('note=128') > -1:

            gs+=1

        if str(mid_extract).find('note=9') > -1 or str(mid_extract).find('note=21') > -1 or str(mid_extract).find('note=33') > -1 or str(mid_extract).find('note=45') > -1 or str(mid_extract).find('note=57') > -1 or str(mid_extract).find('note=69') > -1 or str(mid_extract).find('note=81') > -1 or str(mid_extract).find('note=93') > -1 or str(mid_extract).find('note=105') > -1 or str(mid_extract).find('note=117') > -1 :

            a+=1

        if str(mid_extract).find('note=10') > -1 or str(mid_extract).find('note=22') > -1 or str(mid_extract).find('note=34') > -1 or str(mid_extract).find('note=46') > -1 or str(mid_extract).find('note=58') > -1 or str(mid_extract).find('note=70') > -1 or str(mid_extract).find('note=82') > -1 or str(mid_extract).find('note=94') > -1 or str(mid_extract).find('note=106') > -1 or str(mid_extract).find('note=118') > -1 :

            asu+=1

        if str(mid_extract).find('note=11') > -1 or str(mid_extract).find('note=23') > -1 or str(mid_extract).find('note=35') > -1 or str(mid_extract).find('note=47') > -1 or str(mid_extract).find('note=59') > -1 or str(mid_extract).find('note=71') > -1 or str(mid_extract).find('note=83') > -1 or str(mid_extract).find('note=95') > -1 or str(mid_extract).find('note=108') > -1 or str(mid_extract).find('note=119') > -1 :

            b+=1

    print((100/soma)*c,(100/soma)*cs,(100/soma)*d,(100/soma)*ds,(100/soma)*e,(100/soma)*f,(100/soma)*fs,(100/soma)*g,(100/soma)*gs,(100/soma)*a,(100/soma)*asu,(100/soma)*b)
            