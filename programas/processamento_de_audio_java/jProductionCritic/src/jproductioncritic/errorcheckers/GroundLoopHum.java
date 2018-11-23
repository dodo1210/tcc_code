/**
 * GroundLoopHum.java
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
import java.util.ArrayList;

/**
 * <p>This class is used to examine audio samples and report any technical errors that are found associated
 * with ground loop hums.</p>
 * 
 * <p>A ground loop hum is a kind of electrical noise that can be picked up when using imperfectly configured
 * or operated studios. Such noise consists of a hum at the AC frequency of the power supply (and its integer
 * multiples), which is generally either 50 Hz or 60 Hz, depending on where one is.</p>
 * 
 * <p>The algorithm used here to detect ground loops consists of first finding the power spectrum value for 
 * the bin holding each of the possible ground loop fundamental frequencies (50 Hz and 60 Hz). An error is
 * reported whenever this value exceeds a user-defined threshold. Large analysis windows are used in order to
 * not generate false positives from fundamental frequencies of shorter desirable bass notes at the ground 
 * loop frequencies. If there are multiple channels in the audio being examined, then each channel is checked 
 * separately, and an error report is generated if a problem is detected on one or multiple channels.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): long_window_size 
 * (the number of samples that each analysis window should consist of) and ground_loop_hum_maximum_power
 * (the maximum power spectrum value that the power spectrum bin holding a ground loop fundamental frequency
 * may have in order to avoid the detection of a ground loop hum error).</p>
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
public class GroundLoopHum
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The maximum power spectrum value that the power spectrum bin holding a ground loop fundamental 
	 * frequency may have in order to avoid the detection of a ground loop hum error. This value must be more
	 * than 0.0.
	 */
	private	double	ground_loop_hum_maximum_power;
	
	
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
	public GroundLoopHum(ConfigurationSettings preferences)
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
			if (preferences.getConfigurationSetting("ground_loop_hum_maximum_power") != null)
				ground_loop_hum_maximum_power = Double.valueOf(preferences.getConfigurationSetting("ground_loop_hum_maximum_power"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Ground Loop Hum";
		is_instantaneous = false;
		should_check_tag = "check_ground_loop_hum";
		should_check_by_default = true;
		
		long_window_size = 262144;
		
		ground_loop_hum_maximum_power = 45.0;
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
										{"ground_loop_hum_maximum_power", String.valueOf(ground_loop_hum_maximum_power) } };
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
		if (ground_loop_hum_maximum_power <= 0.0)
			problems.add("ground_loop_hum_maximum_power is set to " + ground_loop_hum_maximum_power + ", but must be more than 0.0.");
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

		// The ground loop fundamental frequencies to check
		double[] freqs_to_check = { 50.0, 60.0 };

		// Calculate the power spectrum values
		double[][][] pow_spec_values_at_freq = new double[freqs_to_check.length][][];
		for (int freq = 0; freq < freqs_to_check.length; freq++)
		{
			pow_spec_values_at_freq[freq] = JaudioFeatures.getPowerSpectrumValueAtFreq( audio,
					                                                                    freqs_to_check[freq],
																						long_window_size );
		}

		// Check every window in every channel for each of the ground loop fundamental frequencies and
		// generate an error report where appropriate
		for (int chan = 0; chan < number_channels; chan++)
			for (int wind = 0; wind < pow_spec_values_at_freq[0][chan].length; wind++)
				for (int freq = 0; freq < freqs_to_check.length; freq++)
					if (pow_spec_values_at_freq[freq][chan][wind] > ground_loop_hum_maximum_power)
					{
						int start_sample = wind * long_window_size;
						int end_sample = start_sample + long_window_size - 1;
						int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
						int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

						String severity = "Mild";
						if (pow_spec_values_at_freq[freq][chan][wind] > (1.5 * ground_loop_hum_maximum_power))
							severity = "Moderate";
						if (pow_spec_values_at_freq[freq][chan][wind] > (2.0 * ground_loop_hum_maximum_power))
							severity = "Severe";

						ErrorReport this_error = new ErrorReport( title, 
																  is_instantaneous,
																  start_time,
																  end_time,
																  severity );
						errors_per_channel[chan].add(this_error);
					}
	
		// Merge error reports across channels, sort them by start time and merge errors overlapping in time
		ArrayList<ErrorReport> errors_found = getErrorReportsMergedAcrossChannels(errors_per_channel);
		
		// Return the results	
		return errors_found;
	}
}