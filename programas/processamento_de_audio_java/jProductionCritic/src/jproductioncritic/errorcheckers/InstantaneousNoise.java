/**
 * InstantaneousNoise.java
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
 * with instantaneous noise.</p>
 * 
 * <p>Instantaneous noise occurs when there is a sudden change in a signal. It is typically spread over a 
 * broad range of frequencies. Examples include plosive pops due to the improper micing of a singer and noise
 * when a needle jumps on a record.</p>
 * 
 * <p>The detection used here proceeds by breaking the signal into short windows, calculating the magnitude 
 * spectrum of each window and then using this to find the spectral flux from one window to the next. This 
 * spectral flux indicates how much spectral change there is from one window to a next, and a large spectral
 * flux can thus be an indication of undesirable instantaneous noise. An error report is thus generated 
 * whenever the spectral flux is higher than a threshold value for any window to the next. However, this can 
 * result in false positives, as desirable parts of a signal can have a large spectral flux in some cases.
 * This algorithm therefore filters out lower frequencies from consideration in the spectral flux calculation,
 * as undesirable instantaneous noise is typically more evident in the higher frequencies relative to 
 * desirable instantaneous noise. If there are multiple channels in the audio being examined, then each 
 * channel is checked separately, and an error report is generated if a problem is detected on one or
 * multiple channels.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error): analysis_window_size 
 * (the number of samples that each analysis window should consist of), 
 * instantaneous_noise_detection_threshold (the lowest (filtered) spectral flux value that will result in an 
 * error being detected) and instantaneous_noise_highpass_fraction (the fraction of low frequencies to filter
 * out before calculating spectral flux values).</p>
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
public class InstantaneousNoise
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The lowest (filtered) spectral flux value that will result in an instantaneous noise error being
	 * detected. Must be 0.0 or above.
	 */
	private double	instantaneous_noise_detection_threshold;
	
	/**
	 * The fraction of low frequencies to filter out before calculating spectral flux values during the course
	 * of looking for instantaneous noise. No such high pass filtering is done if this value is not above 0
	 * and below 1. For example, a value of 0.7 means that the lowest 70% of the spectral content will be 
	 * filtered out before calculating the spectral flux.
	 */
	private	double	instantaneous_noise_highpass_fraction;
	
	
	/**
	 * The analysis window size (in samples) to use when performing spectral analyses in the course of looking
	 * for this kind of error. This setting is not specific to this ErrorChecker, but is based on the global
	 * configuration setting.
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
	public InstantaneousNoise(ConfigurationSettings preferences)
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
			if (preferences.getConfigurationSetting("instantaneous_noise_detection_threshold") != null)
				instantaneous_noise_detection_threshold = Double.valueOf(preferences.getConfigurationSetting("instantaneous_noise_detection_threshold"));
			if (preferences.getConfigurationSetting("instantaneous_noise_highpass_fraction") != null)
				instantaneous_noise_highpass_fraction = Double.valueOf(preferences.getConfigurationSetting("instantaneous_noise_highpass_fraction"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Instantaneous Noise";
		is_instantaneous = true;
		should_check_tag = "check_instantaneous_noise";
		should_check_by_default = true;
		
		analysis_window_size = 512;
		
		instantaneous_noise_detection_threshold = 0.00006;
		instantaneous_noise_highpass_fraction = 0.61;
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
		                                {"instantaneous_noise_detection_threshold", String.valueOf(instantaneous_noise_detection_threshold) },
										{"instantaneous_noise_highpass_fraction", String.valueOf(instantaneous_noise_highpass_fraction) } };
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
		if (instantaneous_noise_detection_threshold < 0.0)
			problems.add("instantaneous_noise_detection_threshold is set to " + instantaneous_noise_detection_threshold + ", but must be 0.0 or above.");
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
		// The (possibly filtered) spectral flux values indexed by channel and window
		double[][] spec_flux = JaudioFeatures.getSpectralFlux( audio,
															    analysis_window_size,
																instantaneous_noise_highpass_fraction );
		
		// The total number of channels
		int number_of_channels = spec_flux.length;
		
		// Lists of errors detected, separated by the channels on which they are found
		ArrayList<ErrorReport>[] errors_per_channel = (ArrayList<ErrorReport>[]) new ArrayList[number_of_channels];
		for (int i = 0; i < errors_per_channel.length; i++)		
			errors_per_channel[i] = new ArrayList<>();
		
		// Search for errors channel by channel and store them in errors_per_channel
		for (int chan = 0; chan < spec_flux.length; chan++)
		{
			for (int wind = 0; wind < spec_flux[chan].length; wind++)
			{
				if (spec_flux[chan][wind] > instantaneous_noise_detection_threshold)
				{
					int start_time = (int) ((float) (wind * analysis_window_size) / (float) (audio.getSamplingRate()) * 1000.0);
					
					String severity = "Mild";
					if (spec_flux[chan][wind] > (3.0 * instantaneous_noise_detection_threshold))
						severity = "Moderate";
					if (spec_flux[chan][wind] > (6.0 * instantaneous_noise_detection_threshold))
						severity = "Severe";

					ErrorReport this_error = new ErrorReport( title, 
							                                  is_instantaneous,
							                                  start_time,
							                                  -1,
							                                  severity );
					errors_per_channel[chan].add(this_error);
				}
			}
		}

		// Merge error reports across channels, sort them by start time and merge errors overlapping in time
		ArrayList<ErrorReport> errors_found = getErrorReportsMergedAcrossChannels(errors_per_channel);

		// Return the results	
		return errors_found;
	}
}