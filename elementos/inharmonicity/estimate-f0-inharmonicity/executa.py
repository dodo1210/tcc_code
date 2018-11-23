import librosa
import subprocess
import os

arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/felizes/felizes.txt','r')
lines = arq.readlines()
arq.close()
print(lines)
for l in lines:
    #carregamento dos arquivos
    music, erro = l.split("\n",1)
    #VERIFIQUE O CAMINHO, POR FAVOR

    processo = subprocess.call(["python estimate-f0-inharmonicity.py "+music+" 60"], shell=True)
    

