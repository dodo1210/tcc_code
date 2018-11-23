/**
 * IsNotStereo.java
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
 * <p>This class is used to report any errors that are found associated with recordings that are not 
 * stereo.</p>
 * 
 * <p>A stereo recording has two distinct channels, and stereo is still the typical format for music 
 * distribution. Most final mastered tracks should therefore be stereo. However, there are certainly times 
 * when mono or multi-channel (e.g. surround sound) recordings are desirable, so there are times when it is 
 * appropriate to disable this error checker.</p>
 * 
 * <p>An error is generated here simply if the total number of channels in a recording is something other than
 * two.</p>
 * 
 * <p>There are no user-defined preferences for this error checker, other than whether or not to apply it.</p>
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
public class IsNotStereo
	extends ErrorChecker
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public IsNotStereo(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Is Not Stereo";
		is_instantaneous = false;
		should_check_tag = "check_is_not_streo";
		should_check_by_default = true;
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
		String[][] default_settings = { {should_check_tag, String.valueOf(should_check_by_default) } };
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
	{}
	

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

		// Note an error if the recording is not stereo
		if (audio.getNumberChannels() != 2)
		{
			int start_sample = 0;
			int end_sample = audio.getNumberSamplesPerChannel() - 1;
			int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
			int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

			ErrorReport this_error = new ErrorReport( title, 
													  is_instantaneous,
													  start_time,
													  end_time,
													  "Severe" );
			errors_found.add(this_error);
		}

		// Return the results	
		return errors_found;
	}
}