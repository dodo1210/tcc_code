from sklearn.datasets import load_breast_cancer
from sklearn.feature_selection import SelectFwe
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_selection import chi2,f_classif,mutual_info_regression, SelectFdr
from sklearn.feature_selection import SelectKBest
from sklearn import metrics
import pandas as pd

#dados
data=pd.read_csv('/home/douglas/Documentos/tcc_code/resultado/resultados_sem_midi.csv')

x = data[["tempo(bpm)","Intensidade_media_da_musica","espectograma_medio","espectograma_maximo","energia_medio_rmse","brilhantismo_medio_spectral_centroid","Spectral_Rolloff_Point_Overall_Standard_Deviation","Spectral_Flux_Overall_Standard_Deviation","Spectral_Variability_Overall_Standard_Deviation","Spectral_Rolloff_Point_Overall_Average","Spectral_Flux_Overall_Average","Spectral_Variability_Overall_Average","media_do_zero_cross_rate","media_rhythm_strength","spectral_flatness","mfcc","spectral_contrast","bandwidth","percusiviness","magnitude","Cyclic_Tempograms_Based_on_a_short-time_discrete_Fourier_transformation","Strong Rythm","onset","onset_interval","average_onset_frequency","rolloff","power_spectrum","low_energy","harmonic","Cyclic_Tempograms_based_on_the_autocorrelation_function","octave-based_spectral_contrast","chord_variation","harmonic_flux","average_tempo","spectral_kurtosis","loudness_amplitude","sum_beats","strongest_beat","melbands_skewness","melbands_kurtosis","averaga_beat_spectrum","variation_beat_lenght","slope","entropy_spectrum","inharmonicity","pitch_histogram_C","pitch_histogram_C#","pitch_histogram_D","pitch_histogram_D#","pitch_histogram_E","pitch_histogram_F","pitch_histogram_F#","pitch_histogram_G","pitch_histogram_G#","pitch_histogram_A","pitch_histogram_A#","pitch_histogram_B","spectral_crest_factor","loudness_std","chromagram_C","chromagram_C#","chromagram_D","chromagram_D#","chromagram_E","chromagram_F","chromagram_F#","chromagram_G","chromagram_G#","chromagram_A","chromagram_A#","chromagram_B","regularity_rhythm","spectral_spread","spectral_dissonance","salient_pitch","spectral_valleys1","spectral_valleys2","spectral_valleys3","spectral_valleys4","spectral_valleys5","spectral_valleys6","spectral_energy","beats_loudness_band_ratio1","beats_loudness_band_ratio2","beats_loudness_band_ratio3","beats_loudness_band_ratio4","beats_loudness_band_ratio5","beats_loudness_band_ratio6","chord_stregth","hpcp1","hpcp2","hpcp3","hpcp4","hpcp5","hpcp6","hpcp7","hpcp8","hpcp9","hpcp10","hpcp11","hpcp12","hpcp13","hpcp14","hpcp15","hpcp16","hpcp17","hpcp18","hpcp19","hpcp20","hpcp21","hpcp22","hpcp23","hpcp24","hpcp25","hpcp26","hpcp27","hpcp28","hpcp29","hpcp30","hpcp31","hpcp32","hpcp33","hpcp34","hpcp35","hpcp36","dynamics","spectral_flux","loudness_flatness","roughness","spectral_brighness","sharpness","attack_time","attack","chromagram_centroid","loudness_centroid","loudness_flux","smoothness"]]
y = data['Status']
#print(x)
#seleção
X_new = SelectKBest(mutual_info_regression).fit_transform(x,y)

print(X_new.shape)
X_train, X_test, y_train, y_test = train_test_split(X_new, y, test_size=0.2)

#classificação e predição
clf=RandomForestClassifier(n_estimators=100)
clf.fit(X_train,y_train)
y_pred=clf.predict(X_test)
print("Accuracy:",metrics.accuracy_score(y_test, y_pred))
