/**
 * LongSilence.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic.errorcheckers;

import jproductioncritic.ConfigurationSettings;
import jproductioncritic.ErrorReport;
import jproductioncritic.JaudioFeatures;
import mckay.utilities.sound.sampled.AudioMethodsDSP;
import mckay.utilities.sound.sampled.AudioSamples;
import java.util.ArrayList;

/**
 * <p>This class is used to examine audio samples and report any technical errors that are found associated
 * with overly long silences.</p>
 * 
 * <p>Some students make the mistake of leaving too much silence at the beginnings and ends of tracks. 
 * Although it is certainly often desirable to allow notes to gradually die off at ends of tracks, leaving 
 * long silences is typically not a good idea, particularly since many media players automatically insert 
 * their own additional silences between tracks. Audio dropout can also sometimes occur, such that there is an
 * undesirable gap of silence in the middle of a track (e.g. due to a missing file during mastering). Of 
 * course, such silences might sometimes be musically desirable, so it is important to adjust error detection
 * parameters as needed.</p>
 * 
 * <p>The algorithm used here to detect undesirable silences begins by mixing down all channels into a single 
 * channel. The audio is then broken into short windows and the Root Mean Square (RMS) is calculated for each
 * window. RMS provides a good indication of the average power of the signal over the window. All windows with
 * an RMS below the user-specified noise floor are then considered silent, and a list if compiled of the start
 * and end times of all consecutive streaks of such silent windows. Any such set of consecutive windows with a
 * total length greater than a user-defined threshold then generates an error. The user may specify different 
 * threshold for the beginnings, middles and ends of tracks.</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the
 * configuration file (in addition to whether or not to look for this type of error): analysis_window_size 
 * (the number of samples that each analysis window should consist of), long_silence_floor 
 * (the minimum Root Mean Square (RMS) value of a section of sound that will allow it to not be treated as 
 * silence), long_silence_maximum_duration_at_start (the maximum amount of consecutive silence, in
 * milliseconds, which may occur at the very beginning of a track without an error being reported), 
 * long_silence_maximum_duration_dropout (the maximum amount of consecutive silence, in milliseconds, which 
 * may occur anywhere in a track without an error being reported), and long_silence_maximum_duration_at_end 
 * (the maximum amount of consecutive silence, in milliseconds, which may occur at the very end of a track 
 * without an error being reported).</p>
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
public class LongSilence
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The minimum Root Mean Square (RMS) value of a section of sound that will allow it to not be treated as
	 * silence. Must be greater or equal to 0.0.
	 */
	private	double	long_silence_floor;
	
	
	/**
	 * The maximum amount of consecutive silence, in milliseconds, which may occur at the very beginning of a 
	 * track without an error being reported. Must be greater than 0.
	 */
	private int		long_silence_maximum_duration_at_start;
	
	
	/**
	 * The maximum amount of consecutive silence, in milliseconds, which may occur anywhere in a track without
	 * an error being reported. Must be greater than 0.
	 */
	private int		long_silence_maximum_duration_dropout;
	
	
	/**
	 * The maximum amount of consecutive silence, in milliseconds, which may occur at the very end of a track
	 * without an error being reported. Must be greater than 0.
	 */
	private int		long_silence_maximum_duration_at_end;
	

	/**
	 * The analysis window size (in samples) to use when looking for audio dropouts. This setting is not
	 * specific to this ErrorChecker, but is based on the global configuration setting.
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
	public LongSilence(ConfigurationSettings preferences)
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
			if (preferences.getConfigurationSetting("long_silence_floor") != null)
				long_silence_floor = Double.valueOf(preferences.getConfigurationSetting("long_silence_floor"));
			if (preferences.getConfigurationSetting("long_silence_maximum_duration_at_start") != null)
				long_silence_maximum_duration_at_start = Integer.valueOf(preferences.getConfigurationSetting("long_silence_maximum_duration_at_start"));
			if (preferences.getConfigurationSetting("long_silence_maximum_duration_dropout") != null)
				long_silence_maximum_duration_dropout = Integer.valueOf(preferences.getConfigurationSetting("long_silence_maximum_duration_dropout"));
			if (preferences.getConfigurationSetting("long_silence_maximum_duration_at_end") != null)
				long_silence_maximum_duration_at_end = Integer.valueOf(preferences.getConfigurationSetting("long_silence_maximum_duration_at_end"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Long Silence";
		is_instantaneous = false;
		should_check_tag = "check_long_silence";
		should_check_by_default = true;
		
		analysis_window_size = 512;
		
		long_silence_floor = 0.0006;
		long_silence_maximum_duration_at_start = 400;
		long_silence_maximum_duration_dropout = 2500;
		long_silence_maximum_duration_at_end = 1800;
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
										{"long_silence_floor", String.valueOf(long_silence_floor) },
										{"long_silence_maximum_duration_at_start", String.valueOf(long_silence_maximum_duration_at_start) },
										{"long_silence_maximum_duration_dropout", String.valueOf(long_silence_maximum_duration_dropout) },
										{"long_silence_maximum_duration_at_end", String.valueOf(long_silence_maximum_duration_at_end) } };
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
		if (long_silence_floor < 0.0)
			problems.add("long_silence_floor is set to " + long_silence_floor + ", but must be greater or equal to 0.0.");
		if (long_silence_maximum_duration_at_start <= 0)
			problems.add("long_silence_maximum_duration_at_start is set to " + long_silence_maximum_duration_at_start + ", but must be greater than 0.0.");
		if (long_silence_maximum_duration_dropout < 0.0)
			problems.add("long_silence_maximum_duration_dropout is set to " + long_silence_maximum_duration_dropout + ", but must be greater than 0.0.");
		if (long_silence_maximum_duration_at_end < 0.0)
			problems.add("long_silence_maximum_duration_at_end is set to " + long_silence_maximum_duration_at_end + ", but must be greater than 0.0.");
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
		ArrayList<ErrorReport> initial_errors_found = new ArrayList<>();

		// The total number of samples
		int number_of_samples = audio.getNumberSamplesPerChannel();

		// Make sure that are not checking more than the available number of samples
		if ( number_of_samples < convertMillisecondsToSamples(long_silence_maximum_duration_at_start, audio.getSamplingRate()) ||
			 number_of_samples < convertMillisecondsToSamples(long_silence_maximum_duration_dropout, audio.getSamplingRate()) ||				
			 number_of_samples < convertMillisecondsToSamples(long_silence_maximum_duration_at_end, audio.getSamplingRate()) )
			throw new Exception("Audio is too short for specified Long Silence time durations.");

		// The windowed audio samples of all channels mixed into one channel. The last window is zero-padded
		// if necessary. The samples have a minimum value of -1 and a maximum value of +1. The first index
        // pecifies the window and the second index specifies the sample index within the window.
		double[][] windowed_samples = audio.getSampleWindowsMixedDown(analysis_window_size);
		
		// Find the RMS of each window. The index of rms indicates the window.
		double[] rms = new double[windowed_samples.length];
		for (int wind = 0; wind < windowed_samples.length; wind++)
		{
			rms[wind] = JaudioFeatures.getRMS(windowed_samples[wind]);
			// System.err.println("> WINDOW : " + wind + " RMS: " + rms[wind]);
		}
		
		// Annotate all silent windows to silences. The first entry in each pair in silences is the index
		// of the first window that was silent, and the second entry is the the index of the first following
		// window that was not silent.
		ArrayList<int[]> silences = new ArrayList<>();
		int run_start_wind = -1;
		boolean on_streak = false;
		for (int wind = 0; wind < rms.length; wind++)
		{
			if (rms[wind] < long_silence_floor)
			{
				if (!on_streak)
				{
					on_streak = true;
					run_start_wind = wind;
				}
			}
			else
			{
				if (on_streak)
				{
					on_streak = false;
					int[] silences_entry = {run_start_wind, wind};
					silences.add(silences_entry);
				}
			}
		}
		if (on_streak)
		{
			int[] silences_entry = {run_start_wind, rms.length};
			silences.add(silences_entry);
		}
		
		// Return no errors if no silences were found
		if (silences.isEmpty()) return initial_errors_found;
		
		// Generate an error report if there is a silence at the beginning of the audio
		int[] first_silence = silences.get(0);
		if (first_silence[0] == 0)
		{
			generateErrorIfAppropriate( first_silence,
										long_silence_maximum_duration_at_start,
										audio.getSamplingRate(),
										initial_errors_found );
		}
		
		// Generate audio dropout errors in general
		for (int i = 0; i < silences.size(); i++)
		{
			generateErrorIfAppropriate( silences.get(i),
										long_silence_maximum_duration_dropout,
										audio.getSamplingRate(),
										initial_errors_found );
		}
		
		// Generate an error report if there is a silence at the end of the audio
		int[] last_silence = silences.get( silences.size() - 1 );
		if (last_silence[1] == rms.length)
		{
			generateErrorIfAppropriate( last_silence,
										long_silence_maximum_duration_at_end,
										audio.getSamplingRate(),
										initial_errors_found );
		}
		
		// Sort error reports by start time and merge errors overlapping in time
		ArrayList<ErrorReport>[] initial_errors_found_array = (ArrayList<ErrorReport>[]) new ArrayList[1];
		initial_errors_found_array[0] = initial_errors_found;
		ArrayList<ErrorReport> errors_found = getErrorReportsMergedAcrossChannels(initial_errors_found_array);		
		
		// Return the results	
		return errors_found;
	}

	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Convert the given time in milliseconds to its corresponding sample value.
	 * 
	 * @param time			The time in milliseconds to convert.
	 * @param sampling_rate	The sampling rate of the audio.
	 * @return				The sample value corresponding to time.
	 */
	private int convertMillisecondsToSamples(int time, float sampling_rate)
	{
		double seconds = ((double) time) / 1000.0;
		return AudioMethodsDSP.convertTimeToSample(seconds, sampling_rate);
	}
	
	
	/**
	 * Add an error report to errors_found if the silence in silence_report is longer than maximum_duration.
	 * 
	 * @param silence_report	An array of size 2 indicating, first, the window index of the first window
	 *							that was silent and, then, the window index of the first non-silent window
	 *							that followed. Each window is of size analysis_window_size samples.
	 * @param maximum_duration	The maximum amount of consecutive time, in milliseconds, which may occur 
	 *							without an error being reported. Must be greater than 0.
	 * @param sampling_rate		The sampling rate of the audio.
	 * @param errors_found		The list of errors to add to if an error is detected.
	 */
	private void generateErrorIfAppropriate( int[] silence_report,
											 int maximum_duration,
											 float sampling_rate,
										     ArrayList<ErrorReport> errors_found )
	{
		// Find the duration in ms of the silence report
		int windows_long = silence_report[1] - silence_report[0];
		int samples_long = windows_long * analysis_window_size;
		int ms_long = (int) (((float) samples_long / sampling_rate) * 1000.0);
		// System.err.println("\n>LENGTH: " + ms_long);
		
		// Report an error if the silence report is too long
		if (ms_long > maximum_duration)
		{
			int start_sample = silence_report[0] * analysis_window_size;
			int end_sample = silence_report[1] * analysis_window_size - 1;
			int start_time = (int) (((float) start_sample / sampling_rate) * 1000.0);
			int end_time = (int) (((float) end_sample / sampling_rate) * 1000.0);

			String severity = "Mild";
			if (ms_long > 2 * maximum_duration)
				severity = "Moderate";
			if (ms_long > 3 * maximum_duration)
				severity = "Severe";

			ErrorReport this_error = new ErrorReport( title, 
													  is_instantaneous,
													  start_time,
													  end_time,
													  severity );
			errors_found.add(this_error);
		}
	}
}