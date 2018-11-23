/**
 * StereoChannelSimilarity.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic.errorcheckers;

import jproductioncritic.ConfigurationSettings;
import jproductioncritic.ErrorReport;
import mckay.utilities.sound.sampled.AudioSamples;
import mckay.utilities.sound.sampled.AudioMethodsDSP;
import java.util.ArrayList;

/**
 * <p>This class is used to examine audio samples and report any technical errors that are found associated
 * with too much similarity between stereo channels.</p>
 * 
 * <p>Some students do not include enough channel separation in their recordings to create a sufficient sense
 * of stereo space, or even forget to specify panning settings at all, with the result that they end up
 * with identical or near-identical samples in the left and right stereo channels. A stereo channel similarity
 * error is thus generated if the left and right stereo tracks are too similar.</p>
 * 
 * <p>The algorithm used here essentially operates by calculating the average absolute difference between
 * matching samples in the left and right stereo channels (no error reporting is done if the recording is mono
 * or has more than 2 channels). An error is generated if this average difference is less than the 
 * user-specified minimum threshold. It should be noted that each of the stereo channels is separately 
 * normalized before the differences are calculated in order to ensure that an error will still be detected if
 * the only significant difference between the stereo channels is differing channel gains. There are many
 * alternative approaches that could have been used, including spectral approaches, but it was found that this
 * simple approach was quite effective. In particular, it was decided not to use an approach based on
 * cross-correlation, as a phase-delayed version of one signal in the other stereo channel is actually 
 * desirable in many stereo micing approaches, and should not result in an error being reported. A Pearson
 * correlator might also have been an effective choice, but it was not found to be necessary here.</p>
 * 
 * <p>The user may specify the following preference relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error):
 * stereo_channel_similarity_minimum_average_distance (the minimum average difference between matching samples
 * in each of the two separately normalized stereo tracks that must be present in order to avoid the reporting
 * of a stereo channel similarity error).</p>
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
public class StereoChannelSimilarity
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The minimum average difference between matching samples in each of the two separately normalized stereo
	 * tracks that must be present in order to avoid the reporting of a stereo channel similarity error. Must
	 * have a value between 0.0 to 2.0.
 	 */
	private	double	stereo_channel_similarity_minimum_average_distance;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public StereoChannelSimilarity(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("stereo_channel_similarity_minimum_average_distance") != null)
				stereo_channel_similarity_minimum_average_distance = Double.valueOf(preferences.getConfigurationSetting("stereo_channel_similarity_minimum_average_distance"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Stereo Channel Similarity";
		is_instantaneous = false;
		should_check_tag = "check_stereo_channel_similarity";
		should_check_by_default = true;
		
		stereo_channel_similarity_minimum_average_distance = 0.007;
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
										{"stereo_channel_similarity_minimum_average_distance", String.valueOf(stereo_channel_similarity_minimum_average_distance) } };
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
		if (stereo_channel_similarity_minimum_average_distance < 0.0 || stereo_channel_similarity_minimum_average_distance > 2.0)
			problems.add("stereo_channel_similarity_minimum_average_distance is set to " + stereo_channel_similarity_minimum_average_distance + ", but must have a value between 0.0 to 2.0.");
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

		// The total number of channels
		int number_channels = audio.getNumberChannels();
		
		// Return no errors detected if the recording is not stereo
		if (number_channels != 2)
			return errors_found;
		
		// The audio samples to check for errors. These have a minimum value of -1 and a maximum value of +1. 
		// The first index corresponds to the channel and the second index corresponds to the sample number.
		double[][] samples = audio.getSamplesChannelSegregated();
		
		// Get a (separately) normalized copy of each channel
		double[][] samples_norm = new double[samples.length][];
		for (int chan = 0; chan < number_channels; chan++)
			samples_norm[chan] = AudioMethodsDSP.normalizeSamples(samples[chan]);
		
		// The number of samples per channel
		int samples_per_chan = audio.getNumberSamplesPerChannel();
		
		// Find the cumulative absolute difference between all pairs of stereo channels
		double cum_diff = 0.0;
		for (int samp = 0; samp < samples_per_chan; samp++)
			cum_diff += Math.abs( samples_norm[0][samp] - samples_norm[1][samp] );
		
		// Find the average absolute difference between all pairs of stereo channels
		double average_diff = cum_diff / (double) samples_per_chan;
		// System.err.println("\n> AVERAGE DIF: " + average_diff);
		
		// Report an error if appropriate
		if (average_diff < stereo_channel_similarity_minimum_average_distance)
		{
			int start_sample = 0;
			int end_sample = samples_per_chan - 1;
			int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
			int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

			String severity = "Mild";
			if (average_diff < (stereo_channel_similarity_minimum_average_distance / 5))
				severity = "Moderate";
			if (average_diff < (stereo_channel_similarity_minimum_average_distance / 10))
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