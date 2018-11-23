/**
 * DigitalClipping.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic.errorcheckers;

import jproductioncritic.ConfigurationSettings;
import jproductioncritic.ErrorReport;
import mckay.utilities.sound.sampled.AudioSamples;
import java.util.ArrayList;

/**
 * <p>This class is used to examine audio samples and report any technical errors that are found associated
 * with digital clipping.</p>
 * 
 * <p>Digital clipping occurs when a signal exceeds the representational limits of its bit depth. Clipped
 * signals are characterized by flat peaks and troughs, as samples are rounded to maximum and minimum values.
 * Although clipping detection is a common software feature, the popular implementation of simply flagging any 
 * samples at the representational limits is surprisingly naive. This approach has two major problems. 
 * Firstly, a sample that actually should have a value at the representational limit is not in fact clipped, 
 * and such samples are to be expected in normalized signals. Secondly, students may attempt to hide clipping
 * by reducing the master gain in the final mix, such that sample values fall below representational limits
 * (and are thus not flagged) but the signal distortion caused by the clipping remains.</p>
 * 
 * <p>The algorithm used here can overcome these two problems: if a number of adjacent samples beyond a 
 * threshold have an identical signal value (whether or not it is at the representational limit), then report
 * clipping. The number of such consecutive samples also gives an indication of clipping severity. Also, since
 * a signal at very low levels may hold a steady value for a little while even when there is no clipping,
 * clipping is only reported if consecutive samples are identical at a value above a minimum. If there are
 * multiple channels in the audio being examined, then each channel is checked separately, and an error report
 * is generated if a problem is detected on one or multiple channels.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error):
 * digital_clipping_minimum_identical_samples (the minimum number of consecutive samples which must be
 * identical in order for digital clipping to be detected) and digital_clipping_signal_floor (the minimum
 * absolute value that a signal may have (between 0 and 1) in order for digital clipping to be detected).</p>
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
public class DigitalClipping
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The minimum number of consecutive samples which must be identical in order for digital clipping to be 
	 * detected. This value must be more than 1.
	 */
	private	int		digital_clipping_minimum_identical_samples;
	
	
	/**
	 * The minimum (absolute) value that a signal may have (between 0.0 and 1.0) in order for digital clipping
	 * to be detected.
	 */
	private double	digital_clipping_signal_floor;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public DigitalClipping(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("digital_clipping_minimum_identical_samples") != null)
				digital_clipping_minimum_identical_samples = Integer.valueOf(preferences.getConfigurationSetting("digital_clipping_minimum_identical_samples"));
			if (preferences.getConfigurationSetting("digital_clipping_signal_floor") != null)
				digital_clipping_signal_floor = Double.valueOf(preferences.getConfigurationSetting("digital_clipping_signal_floor"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Digital Clipping";
		is_instantaneous = false;
		should_check_tag = "check_digital_clipping";
		should_check_by_default = true;
		
		digital_clipping_minimum_identical_samples = 4;
		digital_clipping_signal_floor = 0.65;
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
										{"digital_clipping_minimum_identical_samples", String.valueOf(digital_clipping_minimum_identical_samples) },
										{"digital_clipping_signal_floor", String.valueOf(digital_clipping_signal_floor) } };
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
		if (digital_clipping_minimum_identical_samples <= 1)
			problems.add("digital_clipping_minimum_identical_samples is set to " + digital_clipping_minimum_identical_samples + ", but must be more than 1.");
		if (digital_clipping_signal_floor < 0.0 || digital_clipping_signal_floor > 1.0)
			problems.add("digital_clipping_signal_floor is set to " + digital_clipping_signal_floor + ", but must be between 0.0 and 1.0.");
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
		// The final error reports which will be returned
		ArrayList<ErrorReport> errors_found = new ArrayList<>();

		// The audio samples to check for errors. These have a minimum value of -1 and a maximum value of +1. 
		// The first index corresponds to the channel and the second index corresponds to the sample number.
		double[][] samples = audio.getSamplesChannelSegregated();
		
		// Return no errors if there is only one sample
		if (samples[0].length <= 1) return errors_found;
		
		// The total number of channels
		int number_channels = samples.length;
		
		// Lists of errors detected, separated by the channels on which they are found
		ArrayList<ErrorReport>[] errors_per_channel = (ArrayList<ErrorReport>[]) new ArrayList[number_channels];
		for (int i = 0; i < errors_per_channel.length; i++)		
			errors_per_channel[i] = new ArrayList<>();

		// Search for errors channel by channel and store them in errors_per_channel
		for (int chan = 0; chan < number_channels; chan++)
		{
			int run_length = 1; // the number of consecutive channels
			for (int samp = 1; samp < samples[chan].length; samp++)
			{
				if ( samples[chan][samp] == samples[chan][samp - 1] && 
					 (samples[chan][samp] >= digital_clipping_signal_floor || samples[chan][samp] <= (-1 * digital_clipping_signal_floor)) )
					run_length++;
				else
				{
					if (run_length >= digital_clipping_minimum_identical_samples) // store an error report
					{
						int end_sample = samp - 1;
						int start_sample = end_sample - run_length + 1;
						int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
						int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);
						
						String severity = "Mild";
						if (run_length > (1.5 * digital_clipping_minimum_identical_samples))
							severity = "Moderate";
						if (run_length > (2.0 * digital_clipping_minimum_identical_samples))
							severity = "Severe";

						ErrorReport this_error = new ErrorReport( title, 
								                                  is_instantaneous,
								                                  start_time,
								                                  end_time,
								                                  severity );
						errors_per_channel[chan].add(this_error);
					}
					
					run_length = 1;
				}
			}
		}
	
		// Merge error reports across channels, sort them by start time and merge errors overlapping in time
		errors_found = getErrorReportsMergedAcrossChannels(errors_per_channel);
		
		// Return the results	
		return errors_found;
	}
}