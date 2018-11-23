from __future__ import division
from math import log,ceil,pi,sin,cos,floor
import scipy as sc
from scipy.io import loadmat
import numpy as np
import librosa
import sys
import os
import operator
import argparse
import music21
from music21 import *

import warnings
warnings.filterwarnings("ignore")


# Estimate the noise level:
# based on "ADAPTIVE NOISE LEVEL ESTIMATION" and "A PARAMETRIC MODEL OF PIANO TUNING"
# We chose a 300Hz median filtering on the magnitude spectrum S(f) to estimate (f).
# Finally, we define the noise level in each band NL(f) as the magnitude
# such that the cumulative distribution function is equal to a
# given threshold T, set to T = 0.9999
# a good estimator for sigma is median/ln(4)**0.5
# according to the Rayleigh distribution, NL = sigma*(-2*log(1-T))**0.5

# X: Discrete Fourier Transform
# fR: frequency resolution
# fb: filter bandwidth

def noiseLevelEstimation(X,fR,fb):
    for k in range(len(X)):
        # [-fb/2 fb/2] median filter
        # NL[k] = np.median(X[max(1,int(k-round(fb/2/fR))):min(len(X),int(k+round(fb/2/fR)))])
        NL[k] = np.median(X[max(0,int(k-round(fb/2/fR))):min(len(X),int(k+round(fb/2/fR)))+1])
        NL[k] = NL[k]/(log(4)**0.5)*(-2*log(10**(-4)))**0.5;
    
    return NL


# generate the mainlobes of hamming windows with different window sizes

def mainLobe(tao):
    for i in range(len(tao)):
        gt.append([])
        dgt.append([])
        t = tao[i]
        fg = np.arange(-2/t,2/t+1,1)
        for j in fg:
            gt[i].append(sin(pi*j*t)/(pi*t*(j-t**2*j**3)));    
            dgt[i].append((pi*t*cos(pi*j*t)*(j-t**2*j**3)-sin(pi*j*t)*(1-3*t**2*j**2))/((pi*t*(j-t**2*j**3))**2)/j)
        
        gt[i][int(ceil(len(gt[i])/2))-1] = 1;
        gt[i][int(ceil(len(gt[i])/4))-1] = 1/2;
        gt[i][int(ceil(3*len(gt[i])/4))-1] = 1/2;
            
        dgt[i][int(ceil(len(gt[i])/2))-1] = (6-pi**2)/(3*pi)*t;
        dgt[i][int(ceil(len(gt[i])/4))-1]  = -(3*t)/(4*pi);
        dgt[i][int(ceil(3*len(gt[i])/4))-1] = -(3*t)/(4*pi);
        
    return gt,dgt

# based on "A parameteric model and estimation techniques for the inharmonicity and tuning of the piano"
# X: Discrete Fourier Transform (normalized to a max. of 1)
# gt,dgt: the main lobe
# beta,lambda,iter: parameters
# B: inharmonicity factor
# f0: fundamental frequency
# N: partial numbers
# NL: noise level
    
def inharmonicity(X,gt,dgt,beta,_lambda,_iter,B,f0,N,NL):
    K = len(X)
    n = np.arange(1,N+1)
    f = np.zeros(len(n))

    for i in range(len(n)):
        f[i] = n[i]*f0*(1+B*n[i]**2)**0.5

    N = min(N,len(np.where(f<K-200)[0])) 
    n = np.arange(1,N+1)
    ftemp = np.zeros(len(n))
    a = np.zeros(len(n))
    for i in range(len(n)):
        ftemp[i] = f[i]
        a[i] = 1

    f = ftemp
    h = 1
    fk = np.arange(1,K+1)

    for itTime in range(_iter):

        it = itTime+1
        # choose the window function
        i = min(len(gt), int(ceil(it/5)))
        gtao = gt[i-1]
        dgtao = dgt[i-1]

        # claculare the reconstruction
        g = np.zeros((K,N))
        f = [int(round(freq)) for freq in f]

        for i in range(len(n)):
            g[f[i]-1,i] = 1
            g[:,i] = np.convolve(g[:,i],gtao,'same')

        g = g[:K,:]
        W = g.dot(a)+np.finfo(float).eps
        V = W.dot(h)

        # update a

        HV = V**(beta-1)*h
        HVX = V**(beta-2)*X*h
        P0a = HV.dot(g)
        Q0a = HVX.dot(g)
        a = a*Q0a/P0a

        # update W

        g = np.zeros((K,N))
        dg = np.zeros((K,N))

        for i in range(len(n)):
            g[f[i]-1,i] = 1
            g[:,i] = np.convolve(g[:,i],gtao,'same')
            dg[f[i]-1,i] = 1
            dg[:,i] = np.convolve(dg[:,i],dgtao,'same')

        g = g[:len(X),:]
        dg = dg[:len(X),:]
        W = g.dot(a)+np.finfo(float).eps
        V = W.dot(h)

        # update f

        nN = range(1,N+1)
        P0f = - a*((fk*HV).dot(dg)) - a*f*(HVX.dot(dg))
        Q0f = - a*((fk*HVX).dot(dg)) - a*f*(HV.dot(dg))

        P1f = np.zeros(len(nN))
        Q1f = np.zeros(len(nN))
        for i in range(len(nN)):
            P1f[i] = 2*f[i]
            Q1f[i] = 2*nN[i]*f0*(1+B*nN[i]**2)**0.5

        f = f*(Q0f+_lambda[int(ceil(it/100))-1]*Q1f)/(P0f+_lambda[int(ceil(it/100))-1]*P1f);

        # update B and F0

        ftemp = np.zeros(len(f))
        for i in range(len(f)):
            ftemp[i] = round(f[i])

        NLtemp = np.zeros(len(ftemp))
        for i in range(len(ftemp)):
            NLtemp[i] = NL[int(ftemp[i])]

        nNL = np.where(a > NLtemp)[0]
        if nNL.size == 0:
            nNL = np.asarray([1])
        else:
            nNL = nNL+1

        u = range(1,31)
        for i in range(len(u)):
            ftemp = np.zeros(len(nNL))
            for i in range(len(nNL)):
                ftemp[i] = f[nNL[i]-1]

            f0 = sum(ftemp*nNL*((1+B*nNL**2)**0.5))/sum(nNL**2*(1+B*nNL**2))

            ib = range(1,21)
            for b in range(len(ib)):
                P1B = f0*sum(nNL**4)
                Q1B = sum(nNL**3*ftemp/((1+B*nNL**2)**0.5))
                B = B*Q1B/P1B
    
    ftemp = np.zeros(len(f))
    for i in range(len(f)):
        ftemp[i] = round(f[i])
    f = ftemp
    
    for i in range(len(f)):
        idx, c = max(enumerate(X[int(f[i]-2-1):int(f[i]+2+1)]), key=operator.itemgetter(1))
        f[i]=f[i]+idx+1-3

    return a, f, B, f0,V


if __name__=="__main__":
    argparser = argparse.ArgumentParser(
        description = "Estimate fundamental frequency and inharmonicity coefficient from an isolated piano note."
    )

    arq = open('/home/douglas/Documentos/tcc_code/musicas/wav/tristes/tristes.txt','r')
    lines = arq.readlines()
    arq.close()
    cont =0 
    for l in lines:
        #carregamento dos arquivos
        music, erro = l.split("\n",1)
        
        arq = open('/home/douglas/Documentos/tcc_code/musicas/midi/tristes.txt','r')
        lines = arq.readlines()
        arq.close()
        musics, erro = lines[cont].split("\n",1)
        score = music21.converter.parse('/home/douglas/Documentos/tcc_code/musicas/midi/tristes/'+musics)
        tom = score.analyze('key')
        frequencia = note.Note(tom.tonic.name)
        #print(str(key.pitchToSharps(str(tom.tonic.name),str(tom.mode))))

        if str(tom.tonic.name).find("C")>-1:
            midiNum = 36
        if str(tom.tonic.name).find("C#")>-1:
            midiNum = 37
        if str(tom.tonic.name).find("D")>-1 or str(tom.tonic.name).find("D-")>-1:
            midiNum = 38
        if str(tom.tonic.name).find("D#")>-1 or str(tom.tonic.name).find("E-")>-1:
            midiNum = 39
        if str(tom.tonic.name).find("E")>-1:
            midiNum = 40
        if str(tom.tonic.name).find("F")>-1:
            midiNum = 41
        if str(tom.tonic.name).find("F#")>-1 or str(tom.tonic.name).find("G-")>-1:
            midiNum = 42
        if str(tom.tonic.name).find("G")>-1:
            midiNum = 43
        if str(tom.tonic.name).find("G#")>-1 or str(tom.tonic.name).find("A-")>-1:
            midiNum = 44
        if str(tom.tonic.name).find("A")>-1:
            midiNum = 45
        if str(tom.tonic.name).find("A#")>-1 or str(tom.tonic.name).find("B-")>-1:
            midiNum = 46
        if str(tom.tonic.name).find("B")>-1:
            midiNum = 47

	# noteFileS = [index for index, value in enumerate(wav_file) if value == '/'][-1]
	# midiNumS = [index for index, value in enumerate(wav_file) if value == 'm'][-1]
	# midiNumE = [index for index, value in enumerate(wav_file) if value == '.'][-1]
	# midiNum = int(wav_file[midiNumS+1:midiNumE])
	# noteFile = wav_file[noteFileS+1:midiNumE]

        # x = MonoLoader(filename=wav_file)()
        fs = 44100
        x, sr = librosa.load('/home/douglas/Documentos/tcc_code/musicas/wav/tristes_30/'+music,sr=44100)

        B = loadmat('./data/initialB.mat')
        B = B['B']

        fR = 2 # frequency resolution
        K = int(round(fs/fR/3)) # K is the number of frequency bins, frequency range 0-fs/3
        N = 30 # N is the number of partials
        # T is the number of time frames

        R = 88 # R is the number of piano notes
        r = range(R) # MIDI index 1:R 
        f0 = np.zeros(88);
        for i in r:
            f0[i] = 440*2**((i+1-49)/12)/fR
        # f0(r) = 440*2.^((r-49)/12) fundamental frequency for piano notes
        # f0 = f0/fR frequency index of f0

        beta = 1
        _lambda = np.asarray([0.125,5*10**(-3)])
        _iter = 150

        tao = np.asarray([1/60, 1/40, 1/30, 1/20, 1/10, 1/8, 1/6, 1/4, 1/2, 1]) # window size

        S = np.zeros((K,R))
        B1 = np.zeros(88)

        r = midiNum-20-1 # Piano note index
        onset = 0.5*fs

        # calculate the Discrete Fourier Transform

        frame = x[int(onset):int(onset+fs/2)]
        frame = np.hamming(fs/2)*frame
        X = abs(np.fft.fft(frame, int(fs/fR)));
        X = X[:K]
        X = X/max(X)
        S[:,r] = X

        NL = np.zeros(K)
        NL = noiseLevelEstimation(X,fR,300)

        gt = []
        dgt = []
        gt, dgt = mainLobe(tao[10-int(ceil(r/10))-1:])

        a, f, B1[r], f0[r],V = inharmonicity(X,gt,dgt,beta,_lambda,_iter,B[r],f0[r],N,NL)
        f0_final =  f0[r]*fR

        print("%s" %B1[r])
        cont+=1

