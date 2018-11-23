/**
 * InsufficientDynamicRange.java
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
 * with an insufficiently large dynamic range.</p>
 * 
 * <p>An insufficient use of the available dynamic range error indicates that a signal is too quiet throughout
 * its entirety, which is to say that its highest absolute sample value is too far below the maximum absolute 
 * sample value afforded by the signal's bit depth. This kind of error does not occur if part of a signal is 
 * quiet (which can be desirable), but rather if all parts of a signal are too quiet. This is a common mistake
 * made by students who keep gains excessively low due to fear of clipping. Students then sometimes exacerbate 
 * this by forgetting to normalize their work during mastering (which can be desirable in order to achieve 
 * relatively consistent volumes when listening to multiple recordings sequentially).</p>
 * 
 * <p>The algorithm used here to detect this kind of problem is to find the maximum absolute sample value in
 * each channel and report an error if it is below a user-defined threshold. An error is reported if this
 * problem is detected in one or more channels.</p>
 * 
 * <p>The user may specify the following preference relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): 
 * insufficient_dynamic_range_minimum_highest_absolute_sample_value (the minimum absolute value (above 0.0 
 * and no higher than 1.0) that the sample with the highest absolute value in each channel must have in order 
 * to avoid the detection of an insufficient dynamic range error.</p>
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
public class InsufficientDynamicRange
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The minimum absolute value that the sample with the highest absolute value in each channel must have in
	 * order to avoid the detection of an insufficient dynamic range error. Must be above 0.0 and no higher
	 * than 1.0, otherwise this error will not be looked for.
	 */
	private	double		insufficient_dynamic_range_minimum_highest_absolute_sample_value;
	
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public InsufficientDynamicRange(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("insufficient_dynamic_range_minimum_highest_absolute_sample_value") != null)
				insufficient_dynamic_range_minimum_highest_absolute_sample_value = Double.valueOf(preferences.getConfigurationSetting("insufficient_dynamic_range_minimum_highest_absolute_sample_value"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Insufficient Dynamic Range";
		is_instantaneous = false;
		should_check_tag = "check_insufficient_dynamic_range";
		should_check_by_default = true;
		
		insufficient_dynamic_range_minimum_highest_absolute_sample_value = 0.88;
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
										{"insufficient_dynamic_range_minimum_highest_absolute_sample_value", String.valueOf(insufficient_dynamic_range_minimum_highest_absolute_sample_value) } };
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
		if (insufficient_dynamic_range_minimum_highest_absolute_sample_value <= 0.0 || insufficient_dynamic_range_minimum_highest_absolute_sample_value > 1.0)
			problems.add("insufficient_dynamic_range_minimum_highest_absolute_sample_value is set to " + insufficient_dynamic_range_minimum_highest_absolute_sample_value + ", but must be above 0.0 and no higher than 1.0.");
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
		
		// Return no errors if an inappropriate value is given for
		// insufficient_dynamic_range_minimum_highest_absolute_sample_value
		if ( insufficient_dynamic_range_minimum_highest_absolute_sample_value <= 0.0 ||
		     insufficient_dynamic_range_minimum_highest_absolute_sample_value > 1.0 )
			return errors_found;

		// The audio samples to check for errors. These have a minimum value of -1 and a maximum value of +1. 
		// The first index corresponds to the channel and the second index corresponds to the sample number.
		double[][] samples = audio.getSamplesChannelSegregated();
		
		// The total number of channels
		int number_channels = samples.length;
		
		// Search for errors channel by channel and store them in errors_found
		for (int chan = 0; chan < number_channels; chan++)
		{
			double highest_sample = 0.0; // the highest absolute sample value found so far in this channel
			
			for (int samp = 1; samp < samples[chan].length; samp++)
			{
				double abs_samp = Math.abs( samples[chan][samp] );
				if (abs_samp > highest_sample)
					highest_sample = abs_samp;
			}
			// System.err.println("\n> HIGHEST SAMPLE: " + highest_sample);
			
			if (highest_sample < insufficient_dynamic_range_minimum_highest_absolute_sample_value)
			{
				int start_sample = 0;
				int end_sample = samples[chan].length;
				int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
				int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

				double offset = insufficient_dynamic_range_minimum_highest_absolute_sample_value - highest_sample;
				String severity = "Mild";
				if (offset > 0.15)
					severity = "Moderate";
				if (offset > 0.3)
					severity = "Severe";

				ErrorReport this_error = new ErrorReport( title, 
														  is_instantaneous,
														  start_time,
														  end_time,
														  severity );
				errors_found.add(this_error);
			}
		}
	
		// Return the results	
		return errors_found;
	}
}