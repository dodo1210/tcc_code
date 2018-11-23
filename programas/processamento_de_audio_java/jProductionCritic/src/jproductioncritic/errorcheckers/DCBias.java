/**
 * DCBias.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic.errorcheckers;

import jproductioncritic.ConfigurationSettings;
import jproductioncritic.ErrorReport;
import mckay.utilities.sound.sampled.AudioSamples;
import mckay.utilities.staticlibraries.MathAndStatsMethods;
import java.util.ArrayList;

/**
 * <p>This class is used to examine audio samples and report any errors that are found associated with a
 * DC bias.</p>
 * 
 * <p>A DC bias occurs in a signal when the signal has a net offset above or below 0, which is to say it has a
 * non-zero average. This can occur in an audio recording in a variety of ways, but the most common way is
 * likely via improper grounding of analog studio equipment. A DC offset such as this can cause clicks at the
 * beginning and end of tracks, and can also cause unnecessary compression of the dynamic range (by causing
 * either peaks or troughs to clip before the other).</p>
 * 
 * <p>The algorithm used here to detect this problem involves simply calculating the average sample value of
 * each channel, and generating an error if it is beyond a user-defined threshold away from zero. If there are
 * multiple channels in the audio being examined, then each channel is checked separately, and an error report
 * is generated if a problem is detected on one or multiple channels.</p>
 * 
 * <p>The user may specify the following preference relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): 
 * dc_bias_maximum_offset (the maximum amount that the average sample value of a channel may differ from 0
 * before an error is reported).</p>
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
public class DCBias
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The maximum amount that the average sample value of a channel may differ from 0 before an error is 
	 * reported). Must be greater that 0.0 and no greater than 1.0.
	 */
	private	double	dc_bias_maximum_offset;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public DCBias(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("dc_bias_maximum_offset") != null)
				dc_bias_maximum_offset = Double.valueOf(preferences.getConfigurationSetting("dc_bias_maximum_offset"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "DC Bias";
		is_instantaneous = false;
		should_check_tag = "check_dc_bias";
		should_check_by_default = true;
		
		dc_bias_maximum_offset = 0.003;
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
										{"dc_bias_maximum_offset", String.valueOf(dc_bias_maximum_offset) } };
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
		if (dc_bias_maximum_offset <= 0.0 || dc_bias_maximum_offset > 1.0)
			problems.add("dc_bias_maximum_offset is set to " + dc_bias_maximum_offset + ", but must be greater that 0.0 and no greater than 1.0.");
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
		// The audio samples to check for errors. These have a minimum value of -1 and a maximum value of +1. 
		// The first index corresponds to the channel and the second index corresponds to the sample number.
		double[][] samples = audio.getSamplesChannelSegregated();
		
		// Lists of errors detected, separated by the channels on which they are found
		ArrayList<ErrorReport>[] errors_per_channel = (ArrayList<ErrorReport>[]) new ArrayList[audio.getNumberChannels()];
		for (int i = 0; i < errors_per_channel.length; i++)		
			errors_per_channel[i] = new ArrayList<>();

		// Search for errors channel by channel and store them in errors_per_channel
		for (int chan = 0; chan < audio.getNumberChannels(); chan++)
		{
			double average_abs_sample_value = Math.abs( MathAndStatsMethods.getAverage(samples[chan]) );
			// System.err.println("\n> AVERAGE SAMPLE VALUE: " + average_abs_sample_value);
			
			if (average_abs_sample_value > dc_bias_maximum_offset)
			{
				int start_sample = 0;
				int end_sample = audio.getNumberSamplesPerChannel() - 1;
				int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
				int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

				String severity = "Mild";
				if (average_abs_sample_value > (2.0 * dc_bias_maximum_offset))
					severity = "Moderate";
				if (average_abs_sample_value > (3.0 * dc_bias_maximum_offset))
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