/**
 * StereoChannelBalance.java
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
 * <p>This class is used to examine audio samples and report any errors that are found associated with an
 * uneven overall loudness balance between the two stereo channels.</p>
 * 
 * <p>Students sometimes fail to properly balance the stereo channels, with the result that one stereo channel
 * is consistently louder than the other. This can lead to an uneven listening experience, especially with
 * headphones.</p>
 * 
 * <p>The algorithm used here is to measure the Root Mean Square (RMS) of each stereo channel as a whole. RMS
 * provides a good indication of the average power of a signal. The difference between the RMS or the two
 * channels is calculated, and an error report is generated if it is above a user-defined threshold. No error
 * is reported if the recording under examination is not stereo.</p>
 * 
 * <p>The user may specify the following preference relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): 
 * stereo_channel_balance_maximum_difference (the maximum amount that the RMS of one stereo channel in its
 * entirety may differ from that of the other without an error being reported).</p>
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
public class StereoChannelBalance
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The maximum amount that the RMS of one stereo channel in its entirety may differ from that of the other
	 * without an error being reported. Must be greater that 0.0.
	 */
	private	double	stereo_channel_balance_maximum_difference;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public StereoChannelBalance(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("stereo_channel_balance_maximum_difference") != null)
				stereo_channel_balance_maximum_difference = Double.valueOf(preferences.getConfigurationSetting("stereo_channel_balance_maximum_difference"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Stereo Channel Balance";
		is_instantaneous = false;
		should_check_tag = "check_stereo_channel_balance";
		should_check_by_default = true;
		
		stereo_channel_balance_maximum_difference = 0.015;
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
										{"stereo_channel_balance_maximum_difference", String.valueOf(stereo_channel_balance_maximum_difference) } };
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
		if (stereo_channel_balance_maximum_difference <= 0.0)
			problems.add("stereo_channel_balance_maximum_difference is set to " + stereo_channel_balance_maximum_difference + ", but must be greater than 0.0.");
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

		// Return no errors detected if the recording is not stereo
		if (audio.getNumberChannels() != 2)
			return errors_found;
		
		// The RMS values indexed by channel and window
		double[][] rms = JaudioFeatures.getRMS(audio, audio.getNumberSamplesPerChannel());
		
		// The difference between the RMS of the two stereo channels
		double difference = Math.abs(rms[0][0] - rms[1][0]);
		// System.err.println("\n> DIFFERENCE:" + difference);
		
		// Report an error if appropriate
		if (difference > stereo_channel_balance_maximum_difference)
		{
			int start_sample = 0;
			int end_sample = audio.getNumberSamplesPerChannel();
			int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
			int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

			String severity = "Mild";
			if (difference > 2.0 * stereo_channel_balance_maximum_difference)
				severity = "Moderate";
			if (difference > 3.0 * stereo_channel_balance_maximum_difference)
				severity = "Severe";

			ErrorReport this_error = new ErrorReport( title, 
													  is_instantaneous,
													  start_time,
													  end_time,
													  severity );
			errors_found.add(this_error);
		}

		// Return the results	
		return errors_found;
	}
}