/**
 * Duration.java
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
 * <p>This class is used to report any errors that are found associated with recordings that are too long or
 * too short.</p>
 * 
 * <p>Some recording assignments have minimum and maximum track durations, so the purpose of this error 
 * checker is to ensure that these constraints are respected. Length constraints can also be present in the
 * professional world, particularly in advertisements or music intended to accompany TV shows or movies.</p>
 * 
 * <p>An error is generated here simply if the total duration of a recording falls outside the user-defined
 * boundaries.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error):
 * duration_minimum_length (the minimum duration, in seconds, that a recording must have in order to avoid an
 * error being reported) and duration_maximum_length (the maximum duration, in seconds, that a recording may 
 * have in order to avoid an error being reported).</p>
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
public class Duration
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The minimum duration, in seconds, that a recording must have in order to avoid an error being
	 * reported. This value must be 0.0 or greater.
	 */
	private	double	duration_minimum_length;
	
	
	/**
	 * The maximum duration, in seconds, that a recording may have in order to avoid an error being
	 * reported. This value must be greater than 0.0.
	 */
	private	double	duration_maximum_length;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public Duration(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("duration_minimum_length") != null)
				duration_minimum_length = Double.valueOf(preferences.getConfigurationSetting("duration_minimum_length"));
			if (preferences.getConfigurationSetting("duration_maximum_length") != null)
				duration_maximum_length = Double.valueOf(preferences.getConfigurationSetting("duration_maximum_length"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Duration";
		is_instantaneous = false;
		should_check_tag = "check_duration";
		should_check_by_default = true;
		
		duration_minimum_length = 0.0;
		duration_maximum_length = 5400.0;
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
										{"duration_minimum_length", String.valueOf(duration_minimum_length) },
										{"duration_maximum_length", String.valueOf(duration_maximum_length) } };
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
		if (duration_minimum_length < 0.0)
			problems.add("duration_minimum_length is set to " + duration_minimum_length + ", but must be 0.0 or greater.");
		if (duration_maximum_length <= 0.0)
			problems.add("duration_maximum_length is set to " + duration_maximum_length + ", but must be greater than 0.0.");
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

		// The duration of the recording in seconds
		double duration_secs = audio.getDuration();
		
		// Report an error if one or more problems are found
		if (duration_secs <  duration_minimum_length || duration_secs > duration_maximum_length)
		{
			int start_sample = 0;
			int end_sample = audio.getNumberSamplesPerChannel() - 1;
			int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
			int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

			String severity = "Mild";
			if (duration_secs <  duration_minimum_length)
			{
				if (duration_secs < (0.85 * duration_minimum_length))
					severity = "Moderate";
				if (duration_secs < (0.7 * duration_minimum_length))
					severity = "Severe";
			}
			else if (duration_secs > duration_maximum_length)
			{
				if (duration_secs > (1.15 * duration_maximum_length))
					severity = "Moderate";
				if (duration_secs > (1.3 * duration_maximum_length))
					severity = "Severe";
			}
				
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