/**
 * InsufficientVarietyInDynamics.java
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
 * <p>This class is used to examine audio samples and report any errors that are found associated with an
 * insufficiently high variety in dynamics.</p>
 *
 * <p>Certain genres of music (e.g. Romantic classical music) typically stylistically require that there be 
 * some significant contrast in dynamics throughout a piece. In other words, some parts should be louder than 
 * others. There may therefore be insufficient variety in dynamics if all parts of a signal are of very similar
 * loudness. It is important to emphasize, however, that this is more of a stylistic consideration than a 
 * technical error, and limited variety in dynamics can actually be stylistically desirable in some genres
 * of music (e.g. dance pop).</p>
 * 
 * <p>The algorithm used here breaks each channel of a recording into a sequence of windows, and the Root Mean 
 * Square (RMS) is calculated for each window. RMS provides a good indication of the average power of the
 * signal over the window. The standard deviation of these RMS values is then calculated for each channel. An 
 * error is reported if this standard deviation is below the user defined threshold for one or more 
 * channels.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): analysis_window_size 
 * (the number of samples that each analysis window should consist of) and
 * insufficient_variety_in_dynamics_minimum_stddev (the minimum standard deviation of windowed RMS values in
 * each channel necessary to avoid the reporting of an insufficient variety in dynamics error).</p>
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
public class InsufficientVarietyInDynamics
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The minimum standard deviation of windowed RMS values in each channel necessary to avoid the reporting
	 * of an insufficient variety in dynamics error. This value must be 0.0 or more.
	 */
	private	double	insufficient_variety_in_dynamics_minimum_stddev;
	
	
	/**
	 * The analysis window size (in samples) to use when calculating RMS values. This setting is not specific
	 * to this ErrorChecker, but is based on the global configuration setting. This value must be above 0.
	 */
	private int		analysis_window_size;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public InsufficientVarietyInDynamics(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("analysis_window_size") != null)
				analysis_window_size = Integer.valueOf(preferences.getConfigurationSetting("analysis_window_size"));
			if (preferences.getConfigurationSetting("insufficient_variety_in_dynamics_minimum_stddev") != null)
				insufficient_variety_in_dynamics_minimum_stddev = Double.valueOf(preferences.getConfigurationSetting("insufficient_variety_in_dynamics_minimum_stddev"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Insufficient Variety In Dynamics";
		is_instantaneous = false;
		should_check_tag = "check_insufficient_variety_in_dynamics";
		should_check_by_default = true;
		
		analysis_window_size = 512;
		
		insufficient_variety_in_dynamics_minimum_stddev = 0.016;
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
										{"insufficient_variety_in_dynamics_minimum_stddev", String.valueOf(insufficient_variety_in_dynamics_minimum_stddev) } };
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
		if (insufficient_variety_in_dynamics_minimum_stddev < 0.0)
			problems.add("insufficient_variety_in_dynamics_minimum_stddev is set to " + insufficient_variety_in_dynamics_minimum_stddev + ", but must be 0.0 or more.");
		if (analysis_window_size <= 1)
			problems.add("analysis_window_size is set to " + analysis_window_size + ", but must be greater than 1.");
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

		// The RMS values indexed by channel and window
		double[][] rms = JaudioFeatures.getRMS(audio, analysis_window_size);
		
		// Find the standard deviation of the RMS values separately for each channel and report an error
		// if one is found
		for (int chan = 0; chan < audio.getNumberChannels(); chan++)
		{
			// Calculate the standard deviation
			double standard_deviation = MathAndStatsMethods.getStandardDeviation(rms[chan]);

			// Report an error for this channel if appropriate
			if (standard_deviation < insufficient_variety_in_dynamics_minimum_stddev)
			{
				int start_sample = 0;
				int end_sample = audio.getNumberSamplesPerChannel();
				int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
				int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

				String severity = "Mild";
				if (standard_deviation < (insufficient_variety_in_dynamics_minimum_stddev / 1.5))
					severity = "Moderate";
				if (standard_deviation < (insufficient_variety_in_dynamics_minimum_stddev / 3.0))
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