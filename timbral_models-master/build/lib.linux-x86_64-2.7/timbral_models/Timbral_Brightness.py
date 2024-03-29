from __future__ import division
import numpy as np
import soundfile as sf
from . import timbral_util
from scipy.signal import spectrogram


def timbral_brightness(fname, dev_output=False, clip_output=False, phase_correction=False, threshold=0,
                       ratio_crossover=2000, centroid_crossover=100, stepSize=1024, blockSize=2048, minFreq=20):
    """
      This function calculates the apparent Brightness of an audio file.

      Version 0.2

     Required parameters
    :param fname:               Audio filename to be analysed, including full file path and extension.

     Optional parameters
    :param dev_output:          Bool, when False return the brightness, when True return all extracted features.
    :param phase_correction:    Perform phase checking before summing to mono.
    :param threshold:           Threshold below which to ignore the energy in a time window, default to 0.
    :param ratio_crossover:     Crossover frequency for calculating the HF energy ratio, default to 2000 Hz.
    :param centroid_crossover:  Highpass frequency for calculating the spectral centroid, default to 100 Hz.
    :param stepSize:            Step size for calculating spectrogram, default to 1024.
    :param blockSize:           Block size (fft length) for calculating spectrogram, default to 2048.
    :param minFreq:             Frequency for high-pass filtering audio prior to all analysis, default to 20 Hz.

    :return:                    Apparent brightness of audio file, float.

    Copyright 2018 Andy Pearce

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
    """
    # use pysoundfile instead
    audio_samples, fs = sf.read(fname, always_2d=False)

    audio_samples = timbral_util.channel_reduction(audio_samples, phase_correction=phase_correction)

    '''
      Filter audio
    '''
    # highpass audio at minimum frequency
    audio_samples = timbral_util.filter_audio_highpass(audio_samples, crossover=minFreq, fs=fs)
    audio_samples = timbral_util.filter_audio_highpass(audio_samples, crossover=minFreq, fs=fs)
    audio_samples = timbral_util.filter_audio_highpass(audio_samples, crossover=minFreq, fs=fs)

    # get highpass audio at ratio crossover
    ratio_highpass_audio = timbral_util.filter_audio_highpass(audio_samples, ratio_crossover, fs)
    ratio_highpass_audio = timbral_util.filter_audio_highpass(ratio_highpass_audio, ratio_crossover, fs)
    ratio_highpass_audio = timbral_util.filter_audio_highpass(ratio_highpass_audio, ratio_crossover, fs)

    # get highpass audio at centroid crossover
    centroid_highpass_audio = timbral_util.filter_audio_highpass(audio_samples, centroid_crossover, fs)
    centroid_highpass_audio = timbral_util.filter_audio_highpass(centroid_highpass_audio, centroid_crossover, fs)
    centroid_highpass_audio = timbral_util.filter_audio_highpass(centroid_highpass_audio, centroid_crossover, fs)

    # lowpass audio at ratio crossover
    ratio_lowpass_audio = timbral_util.filter_audio_lowpass(audio_samples, ratio_crossover, fs)
    ratio_lowpass_audio = timbral_util.filter_audio_lowpass(ratio_lowpass_audio, ratio_crossover, fs)
    ratio_lowpass_audio = timbral_util.filter_audio_lowpass(ratio_lowpass_audio, ratio_crossover, fs)

    '''
     Get spectrograms and normalise
    '''
    # normalise audio to the maximum value in the unfiltered audio
    ratio_highpass_audio *= (1.0 / max(abs(audio_samples)))
    centroid_highpass_audio *= (1.0 / max(abs(audio_samples)))
    ratio_lowpass_audio *= (1.0 / max(abs(audio_samples)))
    audio_samples *= (1.0 / max(abs(audio_samples)))

    # set FFT parameters
    nfft = blockSize
    hop_size = int(3 * nfft / 4)
    # get spectrogram
    # freq, time, spec = spectrogram(audio_samples, fs, 'hamming', nfft, hop_size, nfft, 'constant', True, 'spectrum')
    ratio_all_freq, ratio_all_time, ratio_all_spec = spectrogram(audio_samples, fs, 'hamming', nfft, hop_size, nfft,
                                                              'constant', True, 'spectrum')
    ratio_hp_freq, ratio_hp_time, ratio_hp_spec = spectrogram(ratio_highpass_audio, fs, 'hamming', nfft, hop_size, nfft,
                                                              'constant', True, 'spectrum')
    centroid_hp_freq, centroid_hp_time, centroid_hp_spec = spectrogram(centroid_highpass_audio, fs, 'hamming', nfft,
                                                                       hop_size, nfft, 'constant', True, 'spectrum')

    # initialise variables for storing data
    all_ratio = []
    all_hp_centroid = []
    all_tpower = []
    all_hp_centroid_tpower = []

    threshold = 0.0
    for idx in range(len(ratio_hp_time)):  # just step through each time window

        # get the current spectrum for this time window
        current_ratio_hp_spec = ratio_hp_spec[:, idx]
        current_ratio_all_spec = ratio_all_spec[:, idx]
        current_centroid_hp_spec = centroid_hp_spec[:, idx]

        # get the tpower
        tpower = np.sum(current_ratio_all_spec)
        hp_tpower = np.sum(current_ratio_hp_spec)
        # check there is energy in the time window before calculating the ratio
        if tpower > threshold:
            # get the ratio
            all_ratio.append(hp_tpower / tpower)
            # store the powef for weighting
            all_tpower.append(tpower)


        # get the t power to assure greater than zero
        hp_centroid_tpower = np.sum(current_centroid_hp_spec)
        if hp_centroid_tpower > threshold:
            # get the centroid
            all_hp_centroid.append(np.sum(current_centroid_hp_spec * centroid_hp_freq[:len(current_centroid_hp_spec)]) /
                                   np.sum(current_centroid_hp_spec))
            # store the tpower for weighting
            all_hp_centroid_tpower.append(hp_centroid_tpower)

    # get the mean values
    mean_ratio = np.mean(all_ratio)
    mean_hp_centroid = np.mean(all_hp_centroid)

    weighted_mean_ratio = np.average(all_ratio, weights=all_tpower)
    weighted_mean_hp_centroid = np.average(all_hp_centroid, weights=all_hp_centroid_tpower)

    if dev_output:
        # return the ratio and centroid
        return np.log10(weighted_mean_ratio), np.log10(weighted_mean_hp_centroid)
    else:
        # perform thye linear regression
        all_metrics = np.ones(4)
        all_metrics[0] = np.log10(weighted_mean_ratio)
        all_metrics[1] = np.log10(weighted_mean_hp_centroid)
        all_metrics[2] = np.log10(weighted_mean_ratio) * np.log10(weighted_mean_hp_centroid)

        coefficients = np.array([-2.9197705625030235, 9.048261758526614, 3.940747859061009, 47.989783427908705])
        bright = np.sum(all_metrics * coefficients)

        if clip_output:
            bright = timbral_util.output_clip(bright)

        return bright
