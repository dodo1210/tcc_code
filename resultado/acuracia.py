from sklearn.datasets import load_breast_cancer
from sklearn.feature_selection import SelectFwe
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_selection import chi2,f_classif,mutual_info_regression, SelectFdr
from sklearn.feature_selection import SelectKBest
from sklearn import metrics
import pandas as pd
import numpy as np

#dados
data=pd.read_csv('resultado.csv')

x = data[["attack","attack_time","average_beat_spectrum","average_onset_frequency","average_tempo","bandwith","beats_loudness_band_ratio1","beats_loudness_band_ratio2","beats_loudness_band_ratio3","beats_loudness_band_ratio4","beats_loudness_band_ratio5","beats_loudness_band_ratio6","spectral_centroid","chord_strength","chromagram1","chromagram2","chromagram3","chromagram4","chromagram5","chromagram6","chromagram7","chromagram8","chromagram9","chromagram10","chromagram11","chromagram12","chromagram_centroid","Cyclic_Tempograms_based_on_the_autocorrelation_function","dissonance","dynamics","energy","entropy_spectrum","espectogram","flatness","harmonic","harmonic_flux","HCPC1","HCPC2","HCPC3","HCPC4","HCPC5","HCPC6","HCPC7","HCPC8","HCPC9","HCPC10","HCPC11","HCPC12","HCPC13","HCPC14","HCPC15","HCPC16","HCPC17","HCPC18","HCPC19","HCPC20","HCPC21","HCPC22","HCPC23","HCPC24","HCPC25","HCPC26","HCPC27","HCPC28","HCPC29","HCPC30","HCPC31","HCPC32","interval_onset","loudness_centroid","loudness_flatness","loudness_flux","loudness_intensidade","loudness_std","low_energy","magnitude","melbands_kurtosis","melbands_skewness","mfcc","onset","percussiviness","power_spectrum","regularity_strength","rhythm_strength","rolloff","salient_pitch","slope","spectral_contrast","spectral_crest_factor","spectral_energy","spectral_kurtosis","spectral_spread","spectral_valley1","spectral_valley2","spectral_valley3","spectral_valley4","spectral_valley5","spectral_valley6","strong_beat","tempo","volume","zcr"]]
y = data['Status']
#seleção
#X_new = SelectKBest(mutual_info_regression).fit_transform(x,y) transfomação e aplicação direta
#transforamação e aplicação por passos
treino = SelectKBest(mutual_info_regression,k=10).fit(x,y)
selecao = treino.transform(x)
scores = treino.scores_
#print(scores)
for i in range(100):
    X_train, X_test, y_train, y_test = train_test_split(x, y, test_size=0.2)

    #classificação e predição
    clf=RandomForestClassifier(n_estimators=100)
    clf.fit(X_train,y_train)
    y_pred=clf.predict(X_test)
    print("Accuracy:",metrics.accuracy_score(y_test, y_pred))
    