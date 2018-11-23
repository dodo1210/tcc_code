/**
 * NarrowbandNoise.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic.errorcheckers;

import jproductioncritic.ConfigurationSettings;
import jproductioncritic.ErrorReport;
import jproductioncritic.JaudioFeatures;
import mckay.utilities.sound.sampled.AudioSamples;
import mckay.utilities.staticlibraries.MathAndStatsMethods;
import java.util.ArrayList;

/**
 * <p>This class is used to examine audio samples and report any technical errors that are found associated
 * with narrowband background noise.</p>
 * 
 * <p>Narrowband noise is a kind of background noise consisting of a relatively narrow spread of frequencies.
 * Tracks can be infiltrated by various types of such sustained noise (as opposed to the more sudden and 
 * short-lived instantaneous noise). Ventilation systems in recording environments and faulty cable shielding 
 * are two of the many possible sources. Unfortunately, detecting such noise in general can be particularly
 * difficult, as it can be hard to distinguish from the musical signal.</p>
 * 
 * <p>The algorithm used here to detect narrowband background noise consists of windowing each channel of the
 * audio and calculating the power spectrum of each resulting window. Large analysis windows are used in order
 * to avoid generating false positives from fundamental frequencies of shorter desirable notes. The average
 * power spectrum bin value is calculated for each window, as is the maximum power spectrum bin value for each
 * window. An error is generated if the maximum value for a window is higher than a user-specified multiple of
 * the average value for the window. If there are multiple channels in the audio being examined, then each 
 * channel is checked separately, and an error report is generated if a problem is detected on one or multiple
 * channels. This approach has the benefit of being particularly sensitive to frequencies that stand out 
 * amongst relatively quiet spectral surroundings, which is when background noise is most evident to the ear. 
 * Unfortunately, this approach is not good at detecting broadband background noise, and can generate false
 * positives for styles of music featuring sustained drones. This is, along with Phasing, one of 
 * jProductionCritic's most underperforming error detectors.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): long_window_size 
 * (the number of samples that each analysis window should consist of) and 
 * narrowband_noise_maximum_spectral_peak_ratio (the maximum multiple of the average power spectrum value over
 * an analysis window that a single power spectrum bin's value may have before a narrowband noise error is 
 * reported).</p>
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
public class NarrowbandNoise
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The maximum multiple of the average power spectrum value over an analysis window that a single power
	 * spectrum bin's value may have before a narrowband noise error is reported. This value must be more
	 * than 1.0.
	 */
	private	double	narrowband_noise_maximum_spectral_peak_ratio;
	
	
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
	public NarrowbandNoise(ConfigurationSettings preferences)
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
			if (preferences.getConfigurationSetting("narrowband_noise_maximum_spectral_peak_ratio") != null)
				narrowband_noise_maximum_spectral_peak_ratio = Double.valueOf(preferences.getConfigurationSetting("narrowband_noise_maximum_spectral_peak_ratio"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Narrowband Noise";
		is_instantaneous = false;
		should_check_tag = "check_narrowband_noise";
		should_check_by_default = true;
		
		long_window_size = 262144;
		
		narrowband_noise_maximum_spectral_peak_ratio = 13500;
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
										{"narrowband_noise_maximum_spectral_peak_ratio", String.valueOf(narrowband_noise_maximum_spectral_peak_ratio) } };
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
		if (narrowband_noise_maximum_spectral_peak_ratio <= 1.0)
			problems.add("narrowband_noise_maximum_spectral_peak_ratio is set to " + narrowband_noise_maximum_spectral_peak_ratio + ", but must be more than 1.0.");
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
		
		// Check every window in every channel for noise and generate an error report where appropriate
		for (int chan = 0; chan < number_channels; chan++)
			for (int wind = 0; wind < power_spectrum[chan].length; wind++)
			{
				// Find the highest allowable power spectrum value for this window
				double pow_spec_average = MathAndStatsMethods.getAverage(power_spectrum[chan][wind]);
				double max_permissible_pow_spec_value = pow_spec_average * narrowband_noise_maximum_spectral_peak_ratio;

				// Find the highest actual power spectrum value for this window
				int pow_spec_max_index = MathAndStatsMethods.getIndexOfLargest(power_spectrum[chan][wind]);
				double pow_spec_max_value = power_spectrum[chan][wind][pow_spec_max_index];
				// System.err.println("\nAverage: " + pow_spec_average + "  Max: " + pow_spec_max_value + "  Ratio: " + (pow_spec_max_value / pow_spec_average));
				
				// Report an error if appropriate
				if (pow_spec_max_value > max_permissible_pow_spec_value)
				{
					int start_sample = wind * long_window_size;
					int end_sample = start_sample + long_window_size - 1;
					int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
					int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

					String severity = "Mild";
					if (pow_spec_max_value > (2.0 * max_permissible_pow_spec_value))
						severity = "Moderate";
					if (pow_spec_max_value > (4.0 * max_permissible_pow_spec_value))
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
}