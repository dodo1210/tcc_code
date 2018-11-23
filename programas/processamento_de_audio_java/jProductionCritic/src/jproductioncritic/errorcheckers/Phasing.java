/**
 * Phasing.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic.errorcheckers;

import jproductioncritic.ConfigurationSettings;
import jproductioncritic.ErrorReport;
import jproductioncritic.JaudioFeatures;
import mckay.utilities.sound.sampled.AudioMethodsDSP;
import mckay.utilities.sound.sampled.AudioSamples;
import mckay.utilities.staticlibraries.MathAndStatsMethods;
import java.util.ArrayList;

/**
 * <p>This class is used to examine audio samples and report any technical errors that are found associated
 * with phasing.</p>
 * 
 * <p>Phasing is a problem that occurs when a signal is mixed with another signal that includes a phase 
 * delayed version of itself. This can occur, for example, when two omnidirectional microphones mapped to the 
 * same channel are too close to each other, or a single microphone is too close to an acoustically 
 * reflective surface. This results in cancellation or reinforcement of various frequencies, depending on the
 * phase offset, which can result in a muddy tone. The effect of this in the frequency domain is similar to
 * that of a comb filter. Although the literature specifies several effective ways to detect phasing before 
 * mixing is carried out, it is much more difficult to automatically detect afterwards, and is easily confused
 * with sometimes desirable audio effects based on short delays, such as flanging.</p>
 * 
 * <p>The algorithm used here involves first calculating the power spectrum of relatively large windows of
 * sound. Each such power spectrum is then collapsed/compressed and the autocorrelation of each compressed 
 * power spectrum is calculated (the compression is necessary for the sake of computational efficiency, given
 * the many bins in the power spectrum resulting from the long analysis windows). Long analysis windows are
 * needed to avoid false positives from held harmonic notes. Autocorrelation essentially measures the 
 * self-similarity of a signal, and in particular generates peaks indicating harmonicities in the signal being 
 * analyzed. Here the "signal" being analyzed is in fact a power spectrum, so the harmonicities highlighted by
 * the autocorrelation indicate regular peaks or troughs in the frequency domain. A signal that has been
 * processed by a comb filter (which is in essence the spectral effect of phasing) will thus have a noticeable
 * autocorrelation peak. A phasing error report is thus generated if the highest peak of the normalized 
 * autocorrelation of the power spectrum is beyond a user-specified threshold (the normalization is performed 
 * in order to ensure balance). If there are multiple channels in the audio being examined, then each channel
 * is checked separately, and an error report is generated if a problem is detected on one or multiple 
 * channels. Although this approach can effectively detect phasing, it unfortunately can also produce false 
 * errors due to very long held harmonic notes or to the application of audio effects like flanging. There is
 * therefore significant room for improvement to this algorithm, and it is perhaps currently the weakest one 
 * used by jProductionCritic.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): long_window_size 
 * (the number of samples that each analysis window should consist of) and 
 * phasing_maximum_autocorrelation (the maximum normalized power spectrum autocorrelation value for any given
 * lag that can be present in order to avoid the reporting of a phasing error).</p>
 * 
 * <p>Extraction on a set of audio samples is performed using the checkForErrors method, which returns
 * error reports indicating the problems found.</p>
 * 
 * <p>The detection settings can be specified via the constructor of this class, and default values for these
 * settings are specified in the setFieldsToDefaults and getDefaultConfigurationSettings methods for this
 * class and for configuration files, respectively.</p>
 * 
 * @author Cory McKay
 */
public class Phasing
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The maximum normalized power spectrum autocorrelation value for any given lag that can be present in 
	 * order to avoid the reporting of a phasing error. This value must be above zero and not more than 1.0.
	 */
	private	double	phasing_maximum_autocorrelation;
	
	
	/**
	 * The analysis window size (in samples) to use when performing spectral analyses in the course of looking
	 * for this kind of error. This setting is not specific to this ErrorChecker, but is based on the global
	 * configuration setting.
	 */
	private int		long_window_size;
	
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public Phasing(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("long_window_size") != null)
				long_window_size = Integer.valueOf(preferences.getConfigurationSetting("long_window_size"));
			if (preferences.getConfigurationSetting("phasing_maximum_autocorrelation") != null)
				phasing_maximum_autocorrelation = Double.valueOf(preferences.getConfigurationSetting("phasing_maximum_autocorrelation"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Phasing";
		is_instantaneous = false;
		should_check_tag = "check_phasing";
		should_check_by_default = false;
		
		long_window_size = 262144;
		
		phasing_maximum_autocorrelation = 0.37;
	}

	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * Returns the configuration settings stored in this object for use in creating a default configuration
	 * file. This includes fields of this class, as well as the the should_check_by_default field inherited
	 * from ErrorChecker (but not the inherited title or is_instantaneous fields).
	 * 
	 * @return	An array of arrays where the first index corresponds to the individual setting. The second
	 *			index stores a given setting's configuration key in its 0th entry, and the setting's 
	 *			configuration value in its 1st entry. Returns null if there are no settings. Note that neither
	 *			keys nor values may include whitespace characters.
	 */
	@Override
	public String[][] getDefaultConfigurationSettings()
	{
		String[][] default_settings = { {should_check_tag, String.valueOf(should_check_by_default) },
										{"phasing_maximum_autocorrelation", String.valueOf(phasing_maximum_autocorrelation) } };
		return default_settings;
	}
	

	/**
	 * Verify that the configuration settings stored in the fields of this ErrorChecker are set to permitted 
	 * values, and add an informative report to the given list of problems if they are invalid. Do nothing if 
	 * the configuration settings stored in the fields are in fact valid.
	 * 
	 * @param problems	A list of configuration settings problems to add to if there are any problems with
	 *					the configuration settings values stored in the fields of this ErrorChecker.
	 */
	@Override
	public void validateConfigurationSettings(ArrayList<String> problems)
	{
		if (phasing_maximum_autocorrelation <= 0.0 || phasing_maximum_autocorrelation > 1.0)
			problems.add("phasing_maximum_autocorrelation is set to " + phasing_maximum_autocorrelation + ", but must be above zero and not more than 1.0.");
		if (long_window_size <= 1)
			problems.add("long_window_size is set to " + long_window_size + ", but must be greater than 1.");
	}
	

	/**
	 * Check the given audio for the technical recording or production error associated with this class and
	 * return any detected errors in the form of ErrorReports.
	 * 
	 * @param audio						The low-level audio data parsed from an audio file that is to be
	 *									checked. May not be null. It is assumed that all channels in audio
	 *									contain an equal number of samples.
	 * @return							All detected errors, sorted by start time and all of the same type. An
	 *									empty ArrayList is returned if none are found. Will NOT be null.
     * @throws Exception				Throws an informative exception if error checking cannot be performed.
	 */
	@Override
	public ArrayList<ErrorReport> checkForErrors(AudioSamples audio)
		throws Exception
	{
		// The total number of channels
		int number_channels = audio.getNumberChannels();

		// Lists of errors detected, separated by the channels on which they are found
		ArrayList<ErrorReport>[] errors_per_channel = (ArrayList<ErrorReport>[]) new ArrayList[number_channels];
		for (int i = 0; i < errors_per_channel.length; i++)		
			errors_per_channel[i] = new ArrayList<>();
		
		// Calculate the power spectrum values
		double[][][] power_spectrum = JaudioFeatures.getPowerSpectrum( audio, long_window_size, null );

		// Check every window in every channel for phasing and generate an error report where appropriate
		for (int chan = 0; chan < number_channels; chan++)
			for (int wind = 0; wind < power_spectrum[chan].length; wind++)
			{
				// Compress the size of the power_spectrum so that it is not too big to process quickly
				power_spectrum[chan][wind] = compressPowerSpectrum(power_spectrum[chan][wind], 4096);
				
				// Calculate the autocorrelation of the power spectrum
				int min_lag = 10;
				int max_lag = power_spectrum[chan][wind].length / 2;
				double[] autocorrelation = AudioMethodsDSP.getAutoCorrelation( power_spectrum[chan][wind],
																			   min_lag, 
																			   max_lag );
				
				// Normalize the autocorrelation
				autocorrelation = MathAndStatsMethods.normalize(autocorrelation);
				
				// Find the highest autocorrelation value
				double highest_ac = 0.0;
				int highest_index = 0;
				for (int i = 0; i < autocorrelation.length; i++)
				{
					// System.err.println("CHAN: " + chan + " WIND: " + wind + " INDEX: " + i + " VALUE: " + autocorrelation[i]);
					if (autocorrelation[i] > highest_ac)
					{
						highest_ac = autocorrelation[i];
						highest_index = i;
					}
				}
				//System.err.println("\nCHAN: " + chan + " WIND: " + wind + " HIGHEST INDEX: " + highest_index + " HIGHEST VALUE: " + highest_ac);
				
				// Generate an error if any of the autocorrelation values are too high
				if (highest_ac > phasing_maximum_autocorrelation)
				{
					int start_sample = wind * long_window_size;
					int end_sample = start_sample + long_window_size - 1;
					int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
					int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

					String severity = "Mild";
					if (highest_ac > (1.3 * phasing_maximum_autocorrelation))
						severity = "Moderate";
					if (highest_ac > (1.6 * phasing_maximum_autocorrelation))
						severity = "Severe";

					ErrorReport this_error = new ErrorReport( title, 
															  is_instantaneous,
															  start_time,
															  end_time,
															  severity );
					errors_per_channel[chan].add(this_error);
				}	
			}		
					
		// Merge error reports across channels, sort them by start time and merge errors overlapping in time
		ArrayList<ErrorReport> errors_found = getErrorReportsMergedAcrossChannels(errors_per_channel);
		
		// Return the results	
		return errors_found;
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Take the given power spectrum and ensure that it is no longer than the given maximum_length. If it
	 * already has a smaller length than maximum_length, then return it unchanged. If it has a length longer
	 * than maximum_length, then merge consecutive bins of the power spectrum such that the length of the
	 * returned array is maximum_length.
	 * 
	 * @param power_spectrum	The power spectrum to potentially compress. The index refers to the bin 
	 *							number.
	 * @param maximum_length	The maximum number of bins that the returned power spectrum can have.
	 * @return					The given power spectrum unchanged if it has a length less than or equal to
	 *							maximum_length, and a compressed version of length maximum_length otherwise.
	 */
	private static double[] compressPowerSpectrum(double[] power_spectrum, int maximum_length)
	{
		if (power_spectrum.length <= maximum_length)
			return power_spectrum;
		else
		{
			double[] compressed_spectrum = new double[maximum_length];
			
			int columns_to_combine = power_spectrum.length / maximum_length;
			
			for (int i = 0; i < compressed_spectrum.length; i++)
			{
				double combined_value = 0;
				for (int j = i * columns_to_combine; j < ((i * columns_to_combine) + columns_to_combine); j++)
					combined_value += Math.sqrt(power_spectrum[j]);
				compressed_spectrum[i] = combined_value * combined_value;
			}
			
			return compressed_spectrum;
		}
	}
}