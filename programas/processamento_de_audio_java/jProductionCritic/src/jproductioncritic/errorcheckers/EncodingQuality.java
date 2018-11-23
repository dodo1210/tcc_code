/**
 * EncodingQuality.java
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
import javax.sound.sampled.AudioFormat;

/**
 * <p>This class is used to report any errors that are found associated with encoding quality.</p>
 * 
 * <p>A poor encoding quality results in a quality of audio that is of insufficient quality as a whole, even
 * if the underlying production work is otherwise good. Some of the possible causes include a bit depth or
 * sampling rate that are too low, or the use of a lossy file format such as an MP3. Typically, the masters 
 * submitted by students should have a high encoding quality, even if they are eventually distributed as
 * lower-quality files.</p>
 * 
 * <p>An error is generated here if the bit depth or sampling rate fall below user-defined minima, or if the
 * audio file being examined is an MP3 and this is not permitted under the user-defined settings.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error):
 * encoding_quality_minimum_sampling_rate (the minimum sampling rate that a recording must have in order to 
 * avoid an error being reported), encoding_quality_minimum_bit_depth (the minimum bit depth that a recording
 * must have in order to avoid an error being reported) and encoding_quality_may_be_mp3 (whether or not a 
 * recording may be an MP3).</p>
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
public class EncodingQuality
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The minimum sampling rate that a recording must have in order to avoid an error being reported. This 
	 * value must be more than 0.0.
	 */
	private	float	encoding_quality_minimum_sampling_rate;
	
	
	/**
	 * The minimum bit depth that a recording must have in order to avoid an error being reported. This value
	 * must be more than 0.
	 */
	private int		encoding_quality_minimum_bit_depth;
	
	
	/**
	 * Whether or not a recording may be an MP3. An error will be generated if this value is true and the
	 * recording being analyzed is an MP3. Should be "true" or "false".
	 */
	private boolean	encoding_quality_may_be_mp3;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public EncodingQuality(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("encoding_quality_minimum_sampling_rate") != null)
				encoding_quality_minimum_sampling_rate = Float.valueOf(preferences.getConfigurationSetting("encoding_quality_minimum_sampling_rate"));
			if (preferences.getConfigurationSetting("encoding_quality_minimum_bit_depth") != null)
				encoding_quality_minimum_bit_depth = Integer.valueOf(preferences.getConfigurationSetting("encoding_quality_minimum_bit_depth"));
			if (preferences.getConfigurationSetting("encoding_quality_may_be_mp3") != null)
				encoding_quality_may_be_mp3 = Boolean.valueOf(preferences.getConfigurationSetting("encoding_quality_may_be_mp3"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Encoding Quality";
		is_instantaneous = false;
		should_check_tag = "check_encoding_quality";
		should_check_by_default = true;
		
		encoding_quality_minimum_sampling_rate = 44100;
		encoding_quality_minimum_bit_depth = 16;
		encoding_quality_may_be_mp3 = false;
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
										{"encoding_quality_minimum_sampling_rate", String.valueOf(encoding_quality_minimum_sampling_rate) },
										{"encoding_quality_minimum_bit_depth", String.valueOf(encoding_quality_minimum_bit_depth) },
										{"encoding_quality_may_be_mp3", String.valueOf(encoding_quality_may_be_mp3) } };
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
		if (encoding_quality_minimum_sampling_rate <= 0)
			problems.add("encoding_quality_minimum_sampling_rate is set to " + encoding_quality_minimum_sampling_rate + ", but must be more than 0.0.");
		if (encoding_quality_minimum_bit_depth <= 0)
			problems.add("encoding_quality_minimum_bit_depth is set to " + encoding_quality_minimum_bit_depth + ", but must be more than 0.");

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

		// The audio format of the audio
		AudioFormat audio_format = audio.getAudioFormat();
		
		// Descriptions of all problems found so far
		String problems_found = "";
		
		// Check for each kind of problem
		if (audio_format.getSampleRate() < encoding_quality_minimum_sampling_rate)
			problems_found += " (sampling rate of " + audio_format.getSampleRate() +  " is too low)";
		if (audio_format.getSampleSizeInBits() < encoding_quality_minimum_bit_depth)
			problems_found += " (bit depth of " + audio_format.getSampleSizeInBits() +  " is too low)";
		if (!encoding_quality_may_be_mp3 && audio.getOriginalAudioFileFormat().getType().toString().equals("MP3"))
			problems_found += " (is encoded as a (lossy) MP3)";

		// Report an error if one or more problems are found
		if (!problems_found.isEmpty())
		{
			int start_sample = 0;
			int end_sample = audio.getNumberSamplesPerChannel() - 1;
			int start_time = (int) (((float) start_sample / (float) audio.getSamplingRate()) * 1000.0);
			int end_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);

			String severity = "Severe";

			ErrorReport this_error = new ErrorReport( title + problems_found, 
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