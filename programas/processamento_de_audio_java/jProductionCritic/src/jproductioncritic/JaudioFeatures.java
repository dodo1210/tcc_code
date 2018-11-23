/**
 * JaudioFeatures.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic;

import mckay.utilities.sound.sampled.AudioSamples;
import mckay.utilities.sound.sampled.FFT;

/**
 * This class holds static methods that port over jAudio features adapted for the purposes of
 * jProductionCritic.
 * 
 * @author	Cory McKay
 */
public class JaudioFeatures
{
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * Calculates the Root Mean Square (RMS) of each window of samples in the specified audio. RMS provides a
	 * good indication of the average power of the signal over the window. RMS is calculated by summing the 
	 * squares of each sample, dividing this by the number of samples in the window, and finding the square 
	 * root of the result.
	 * 
	 * @param audio				The audio samples to calculate the RMS of.
	 * @param window_size		The window size in samples to divide samples into.
	 * @return					The RMS values for the given audio. The first index indicates channel and the
	 *							second indicates window number.
	 * @throws Exception		An informative exception is thrown if a processing error occurs.
	 */
	public static double[][] getRMS( AudioSamples audio, int window_size )
		throws Exception
	{
		// Index the given samples by channel, window and sample
		double[][][] windows = audio.getSampleWindowsChannelSegregated(window_size);
		
		// Calculate the RMS, indexed by channel and window
		double[][] rms = new double[windows.length][];
		for (int chan = 0; chan < windows.length; chan++)
		{
			rms[chan] = new double[windows[chan].length];
			for (int wind = 0; wind < windows[chan].length; wind++)
			{
				double sum = 0.0;
				for (int samp = 0; samp < windows[chan][wind].length; samp++)
					sum += Math.pow(windows[chan][wind][samp], 2);
				rms[chan][wind] = Math.sqrt( sum / windows[chan][wind].length );
			}
		}
		
		// Return the results
		return rms;
	}	
	
	
	/**
	 * Calculates the Root Mean Square (RMS) of the given samples. RMS provides a good indication of the 
	 * average power of a signal. RMS is calculated by summing the squares of each sample, dividing this by
	 * the number of samples, and finding the square root of the result.
	 * 
	 * @param samples		The samples, which must have a minimum value of -1 and a maximum value of +1.		The audio samples to calculate the RMS of.
	 * @return				The RMS value of the given samples.
	 */
	public static double getRMS( double[] samples )
	{
		double sum = 0.0;
		for (int samp = 0; samp < samples.length; samp++)
			sum += Math.pow(samples[samp], 2);
		return Math.sqrt( sum / samples.length );
	}	
	
	
	/**
	 * Calculates the spectral flux between adjacent windows of the given size for the given audio (flux 
	 * values are calculated separately for each channel). Spectral flux is a measure of the amount of 
	 * spectral change in a signal. Spectral flux is calculated by first calculating the difference between
	 * the current value of each magnitude spectrum bin in a given window from the corresponding value of the 
	 * magnitude spectrum of the preceding window. Each of these differences is then squared, and the result
	 * is the sum of the squares. 
	 * 
	 * @param audio				The audio samples to calculate the spectral flux of.
	 * @param window_size		The window size in samples to divide samples into. It is suggested for the
	 *							sake of efficiency that this be a power of 2.
	 * @param highpass_fraction	The fraction of low frequencies to filter out before calculating the spectral
	 *							flux. No such high pass filtering is done if this is not above 0 and below 1.
	 *							For example, a value of 0.7 means that the lowest 70% of the spectral content
	 *							will be filtered out before calculating the spectral flux.
	 * @return					The spectral flux values for the given audio. The first index indicates 
	 *							channel and the second indicates window number.
	 * @throws Exception		An informative exception is thrown if a processing error occurs.
	 */
	public static double[][] getSpectralFlux( AudioSamples audio, int window_size, double highpass_fraction )
		throws Exception
	{
		// Index the given samples by channel, window and sample
		double[][][] windows = audio.getSampleWindowsChannelSegregated(window_size);
		
		// Calculate the magnitude spectra, indexed by channel and window
		double[][][] magnitude_spectrum = new double[windows.length][][];
		for (int chan = 0; chan < windows.length; chan++)
		{
			magnitude_spectrum[chan] = new double[ windows[chan].length ][];
			for (int wind = 0; wind < windows[chan].length; wind++)
			{
				FFT fft = new FFT( windows[chan][wind], null, false, true);
				magnitude_spectrum[chan][wind] = fft.getMagnitudeSpectrum();
			}
		}
		
		// Filter out low frequencies from the magnitude spectrum, if appropriate
		filterSpectrum(magnitude_spectrum, highpass_fraction);
		
		// Calculate the spectral flux, indexed by channel and window
		double[][] spectral_flux = new double[windows.length][];
		for (int chan = 0; chan < windows.length; chan++)
		{
			spectral_flux[chan] = new double[windows[chan].length];
			for (int wind = 0; wind < windows[chan].length; wind++)
			{
				double[] this_spectrum = magnitude_spectrum[chan][wind];
				double[] previous_spectrum;
				if (wind != 0)
					previous_spectrum = magnitude_spectrum[chan][wind - 1];
				else // starting from silence, with no partials present
				{
					previous_spectrum = new double[this_spectrum.length];
					for (int i = 0; i < previous_spectrum.length; i++)
						previous_spectrum[i] = 0.0;
				}

				spectral_flux[chan][wind] = getSpectralFlux(previous_spectrum, this_spectrum);
			}
		}
		
		// Return the results
		return spectral_flux;
	}
	
	
	/**
	 * Calculates the power spectrum of each window of samples in the specified audio. The power spectrum
	 * gives the portion of a signal's power (energy per unit time) that falls with each frequency bin.
	 * 
	 * @param audio			The audio samples to calculate the power spectrum of.
	 * @param window_size	The window size in samples to divide samples into. It is suggested for the
	 *						sake of efficiency that this be a power of 2.
	 * @param bin_labels	This array is filled with the frequency bin labels of the power spectrum
	 *						during this method's processing. Each bin label indicates the lowest frequency
	 *						for the bin. This argument is ignored if null is passed to it. If non-null, a
	 *						double[1][] should be passed to this argument.
	 * @return				The power spectrum values for the given audio. The first index indicates channel,
	 *						the second indicates window number and the third indicates frequency bin.
	 * @throws Exception	An informative exception is thrown if a processing error occurs.
	 */
	public static double[][][] getPowerSpectrum( AudioSamples audio, 
			                                     int window_size,
												 double[][] bin_labels )
		throws Exception
	{
		// Index the given samples by channel, window and sample
		double[][][] windows = audio.getSampleWindowsChannelSegregated(window_size);
		
		// Calculate the power spectra, indexed by channel and window
		double[][][] power_spectrum = new double[windows.length][][];
		for (int chan = 0; chan < windows.length; chan++)
		{
			power_spectrum[chan] = new double[ windows[chan].length ][];
			boolean set_bin_labels = false;
			for (int wind = 0; wind < windows[chan].length; wind++)
			{
				FFT fft = new FFT( windows[chan][wind], null, false, true);
				power_spectrum[chan][wind] = fft.getPowerSpectrum();
				
				if (bin_labels != null && set_bin_labels == false)
				{
					bin_labels[0] = fft.getBinLabels(audio.getSamplingRate());
					set_bin_labels = true;
				}
			}
		}
		
		// Return the results
		return power_spectrum;
	}
	
		
	/**
	 * Calculates the power spectrum value for the bin spanning the specified frequency for each window of
	 * each channel in the specified audio.
	 * 
	 * @param audio			The audio samples to calculate the power spectrum of.
	 * @param frequency		The frequency in Hz to find the power spectrum value for. Must be at a frequency
	 *						that is one half or less of the sampling rate of audio, otherwise an exception
	 *						will be thrown.
	 * @param window_size	The window size in samples to divide samples into. It is suggested for the
	 *						sake of efficiency that this be a power of 2.
	 * @return				The power spectrum value for the bin spanning the specified frequency. The first
	 *						index specifies the channel and the second index specifies the window number.
	 * @throws Exception	An informative exception is thrown if a processing error occurs.
	 */
	public static double[][] getPowerSpectrumValueAtFreq( AudioSamples audio,
			                                              double frequency,
														  int window_size )
		throws Exception
	{
		// Throw exceptions if the requested frequency is unacceptable
		if (frequency > (0.5 * audio.getSamplingRate() ))
			throw new Exception("The requested power spectrum at the frequency of " + frequency + " Hz has too high a frequency for the sampling rate of " + audio.getSamplingRate() + " Hz.");
		if (frequency <= 0)
			throw new Exception("The requested power spectrum at the frequency of " + frequency + " Hz cannot be calculated because a negative frequency was specified.");
		
		// Calculate the power spectrum
		double[][] bin_labels = new double[1][];
		double[][][] power_spectrum =  getPowerSpectrum(audio, window_size, bin_labels);
		
		/*
		// Print out, to standard error, the frequency of the bin with the highest power spectrum and the
		// corresponding power spectrum value
		double largest = 0.0;
		int bin_lab_ind = 0;
		for (int chan = 0; chan < power_spectrum.length; chan++)		
			for (int wind = 0; wind < power_spectrum[chan].length; wind++)
				for (int bin = 0; bin < power_spectrum[chan][wind].length; bin++)
					if (power_spectrum[chan][wind][bin] > largest)
					{
						largest = power_spectrum[chan][wind][bin];
						bin_lab_ind = bin;
					}
		System.err.println("\nMAX FREQ: " + bin_labels[0][bin_lab_ind] + "  MAX POWER SPEC: " + largest);
		*/

		// Find the power spectrum bin for the requested frequency
		int freq_bin_index = bin_labels[0].length - 1;
		for (int bin = 0; bin < bin_labels[0].length - 1; bin++)
			if ( bin_labels[0][bin] <= frequency && bin_labels[0][bin + 1] > frequency )
				freq_bin_index = bin;
		
		// Find the power spectrum value at the requested frequency for each window of each channel
		double[][] results = new double[power_spectrum.length][];
		for (int chan = 0; chan < results.length; chan++)
		{
			results[chan] = new double[power_spectrum[chan].length];
			for (int wind = 0; wind < results[chan].length; wind++)
				results[chan][wind] = power_spectrum[chan][wind][freq_bin_index];
		}
		
		// Return the results
		return results;
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Returns the spectral flux from the first magnitude spectrum to the second.  Spectral flux is a measure
	 * of the amount of spectral change in a signal. Spectral flux is calculated by first calculating the 
	 * difference between the current value of each magnitude spectrum bin in a given window from the 
	 * corresponding value of the  magnitude spectrum of the preceding window. Each of these differences is 
	 * then squared, and the result is the sum of the squares. 
	 * 
	 * @param first_mag_spec	The magnitude spectrum of some window of samples.
	 * @param second_mag_spec	The magnitude spectrum of the window of samples following first_mag_spec.
	 *							Must be the same size as first_mag_spec.
	 * @return					The spectral flux from the first window to the second.
	 */
	private static double getSpectralFlux( double[] first_mag_spec, double[] second_mag_spec )
	{
		double sum = 0.0;
		for (int bin = 0; bin < second_mag_spec.length; bin++)
		{
			double difference = second_mag_spec[bin] - first_mag_spec[bin];
			double differences_squared = difference * difference;
			sum += differences_squared;
		}
		return sum;
	}

	
	/***
	 * Filter the given spectrum with a high-pass filter such that the magnitudes of all frequencies in bins
	 * below cutoff_fraction of the frequency range are set to 0.
	 * 
	 * @param spectrum			The spectra of some windowed multi-channel signal. The first index specifies
	 *							the channel, the second specifies the window and the third specifies the bin
	 *							index of the spectrum.
	 * @param cutoff_fraction	The fraction of bins to set to magnitude 0. Must be above 0.0 and below 1.0,
	 *							otherwise no filtering occurs. For example, a cutoff_fraction of 0.7 means
	 *							that the bins corresponding to the first 70% of the bins is set to 0.
	 */
	private static void filterSpectrum(double[][][] spectrum, double cutoff_fraction)
	{
		if (cutoff_fraction > 0.0 && cutoff_fraction < 1.0)
		{
			int cutoff_bin = (int) (((double) spectrum[0][0].length) * cutoff_fraction);
			for (int chan = 0; chan < spectrum.length; chan++)
				for (int wind = 0; wind < spectrum[chan].length; wind++)
					for (int bin = 0; bin < cutoff_bin; bin++)
						spectrum[chan][wind][bin] = 0.0;
		}
	}
}