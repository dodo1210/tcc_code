/**
 * EditClick.java
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
 * with edit clicks.</p>
 * 
 * <p>An edit click occurs when an improperly treated edit is made, and can result in a discontinuity in the
 * waveform that typically sounds like a click. This can happen when two signals are spliced together, or at
 * the beginnings and ends of tracks (due to a sudden jump in the signal from or to silence). Although there 
 * are a number of techniques that can be used to avoid edit clicks, students and amateurs often neglect to
 * use them.</p>
 * 
 * <p>The algorithm used here detects edit clicks based on windows of only four samples: report an edit click 
 * if a signal jumps in value beyond a threshold from samples 2 to 3, but does not change in value beyond
 * another threshold when progressing from samples 1 to 2 or 3 to 4. Clicks at the beginnings and ends of
 * tracks are found separately by respectively looking for first and last samples that are far from 0 (a
 * different, typically more sensitive threshold is used here than for the window-based detection).  If there 
 * are multiple channels in the audio being examined, then each channel is checked separately, and an error 
 * report is generated if a problem is detected on one or multiple channels. It should be noted that this 
 * algorithm focuses only on a particular kind of edit error. It does not detect edit  errors in general, of 
 * which there are many other types (e.g. a splice involving two segments of audio recorded under very 
 * different reverberant conditions).</p>
 * 
 * <p>The user may specify the following preferences relevant to detecting this type of error in the 
 * configuration file (in addition to whether or not to look for this type of error):
 * edit_click_window_maximum_sample_jump (in a four-sample window, the maximum change in sample value from 
 * sample 2 to sample 3 that the signal may undergo and still avoid the detection of an edit click error
 * (other conditions notwithstanding)), edit_click_window_boundary_fraction (in a four-sample window, the
 * fraction of the edit_click_window_maximum_sample_jump configuration setting value that, when multiplied
 * with the edit_click_window_maximum_sample_jump value, indicates the maximum change in sample value from
 * samples 1 to 2 and from samples 3 to 4 in order for the jump between samples 2 and 3 to be eligible for
 * being counted as an edit click error) and edit_click_boundary_maximum_sample_jump (the maximum absolute
 * sample value that the first and last samples of a signal under examination may have in order to avoid the
 * reporting of an edit click error).</p>
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
public class EditClick
	extends ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * This is, in a four-sample window, the maximum change in sample value from sample 2 to sample 3 that the
	 * signal may undergo and still avoid the detection of an edit click error (other conditions 
	 * notwithstanding). This value must be above 0.0 and not more than 2.0.
	 */
	private	double	edit_click_window_maximum_sample_jump;
	
	
	/**
	 * This is, in a four-sample window, the fraction of the edit_click_window_maximum_sample_jump configuration
	 * setting value that, when multiplied with the edit_click_window_maximum_sample_jump value, indicates the
	 * maximum change in sample value from samples 1 to 2 and from samples 3 to 4 in order for the jump
	 * between samples 2 and 3 to be eligible for being counted as an edit click error. This value must be
	 * above 0.0 and not more than 1.0.
	 */
	private double	edit_click_window_boundary_fraction;
	
	
	/**
	 * The maximum absolute sample value that the first and last samples of a signal under examination may
	 * have in order to avoid the reporting of an edit click error. This value must be between 0.0 and 1.0.
	 */
	private double	edit_click_boundary_maximum_sample_jump;
			
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class and set its fields to default values. If preferences is non-null, 
	 * then some or all defaults are replaced by the specified values.
	 * 
	 * @param preferences 	Execution settings indicating, among other things, whether or not this
	 *						algorithm is to be applied. May be null, in which case default preferences will be
	 *						used exclusively.
	 */
	public EditClick(ConfigurationSettings preferences)
	{
		setFieldsToDefaults();
		
		if (preferences != null)
		{
			if ( preferences.getConfigurationSetting(should_check_tag) != null &&
				 preferences.getConfigurationSetting(should_check_tag).equals("true") )
				should_check_by_default = true;
			else should_check_by_default = false;

			if (preferences.getConfigurationSetting("edit_click_window_maximum_sample_jump") != null)
				edit_click_window_maximum_sample_jump = Double.valueOf(preferences.getConfigurationSetting("edit_click_window_maximum_sample_jump"));
			if (preferences.getConfigurationSetting("edit_click_window_boundary_fraction") != null)
				edit_click_window_boundary_fraction = Double.valueOf(preferences.getConfigurationSetting("edit_click_window_boundary_fraction"));
			if (preferences.getConfigurationSetting("edit_click_boundary_maximum_sample_jump") != null)
				edit_click_boundary_maximum_sample_jump = Double.valueOf(preferences.getConfigurationSetting("edit_click_boundary_maximum_sample_jump"));
		}
	}
		
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Sets all fields to there default values. This includes both all fields inherited from ErrorChecker and
	 * all fields specific to this class.
	 */
	private void setFieldsToDefaults()
	{
		title = "Edit Click";
		is_instantaneous = true;
		should_check_tag = "check_edit_click";
		should_check_by_default = true;
		
		edit_click_window_maximum_sample_jump = 0.48;
		edit_click_window_boundary_fraction = 0.9;
		edit_click_boundary_maximum_sample_jump = 0.02;
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
										{"edit_click_window_maximum_sample_jump", String.valueOf(edit_click_window_maximum_sample_jump) },
										{"edit_click_window_boundary_fraction", String.valueOf(edit_click_window_boundary_fraction) },
										{"edit_click_boundary_maximum_sample_jump", String.valueOf(edit_click_boundary_maximum_sample_jump) } };
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
		if (edit_click_window_maximum_sample_jump <= 0.0 || edit_click_window_maximum_sample_jump > 2.0)
			problems.add("edit_click_window_maximum_sample_jump is set to " + edit_click_window_maximum_sample_jump + ", but must be above 0.0 and not more than 2.0.");
		if (edit_click_window_boundary_fraction <= 0.0 || edit_click_window_boundary_fraction > 1.0)
			problems.add("edit_click_window_boundary_fraction is set to " + edit_click_window_boundary_fraction + ", but must be above 0.0 and not more than 1.0.");
		if (edit_click_boundary_maximum_sample_jump < 0.0 || edit_click_boundary_maximum_sample_jump > 1.0)
			problems.add("edit_click_boundary_maximum_sample_jump is set to " + edit_click_boundary_maximum_sample_jump + ", but must be between 0.0 and 1.0.");
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
		
		// The total number of channels
		int number_channels = samples.length;
		
		// Lists of errors detected, separated by the channels on which they are found
		ArrayList<ErrorReport>[] errors_per_channel = (ArrayList<ErrorReport>[]) new ArrayList[number_channels];
		for (int i = 0; i < errors_per_channel.length; i++)		
			errors_per_channel[i] = new ArrayList<>();

		// Search for errors channel by channel and store them in errors_per_channel
		for (int chan = 0; chan < number_channels; chan++)
		{
			// The first and last sample index values
			int start_sample = 0;
			int end_sample = samples[chan].length - 1;

			// Generate an error if there is a click at the very beginning of the track
			if ( Math.abs(samples[chan][start_sample]) > edit_click_boundary_maximum_sample_jump )
			{
				ErrorReport this_error = new ErrorReport( title, 
														  is_instantaneous,
														  start_sample,
														  -1,
														  getSeverity(Math.abs(samples[chan][start_sample])) );
				errors_per_channel[chan].add(this_error);
			}
			
			// Generate an error if there is a click on a non-boundary sample
			if (samples[chan]. length >= 4)
			{
				for (int samp = start_sample + 1; samp < end_sample - 1; samp++)
				{
					double[] window = { samples[chan][samp - 1], 
						                samples[chan][samp], 
						                samples[chan][samp + 1], 
						                samples[chan][samp + 2] };
					
					double pre_jump = Math.abs( window[1] - window[0] );
					double primary_jump = Math.abs( window[2] - window[1] );
					double post_jump = Math.abs( window[3] - window[2] );
					
					double secondary_jump_maximum = edit_click_window_boundary_fraction * edit_click_window_maximum_sample_jump;
					
					if (primary_jump > edit_click_window_maximum_sample_jump)
					{
						if (pre_jump < secondary_jump_maximum && post_jump < secondary_jump_maximum)
						{
							int start_time = (int) (((float) samp / (float) audio.getSamplingRate()) * 1000.0);
							ErrorReport this_error = new ErrorReport( title, 
																	  is_instantaneous,
																	  start_time,
																	  -1,
																	  getSeverity(primary_jump) );
							errors_per_channel[chan].add(this_error);
						}
					}
				}
			}

			// Generate an error if there is a click at the very end of the track
			if ( Math.abs(samples[chan][end_sample]) > edit_click_boundary_maximum_sample_jump )
			{
				int start_time = (int) (((float) end_sample / (float) audio.getSamplingRate()) * 1000.0);
				ErrorReport this_error = new ErrorReport( title, 
														  is_instantaneous,
														  start_time,
														  -1,
														  getSeverity(Math.abs(samples[chan][end_sample])) );
				errors_per_channel[chan].add(this_error);
			}
		}
	
		// Merge error reports across channels, sort them by start time and merge errors overlapping in time
		ArrayList<ErrorReport> errors_found = getErrorReportsMergedAcrossChannels(errors_per_channel);
		
		// Return the results	
		return errors_found;
	}

	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Returns the severity of a detected edit click based on the given sample_jump.
	 * 
	 * @param sample_jump	The change in sample value from one sample to the next.
	 * @return				The severity of the edit click (Mild, Moderate or Severe).
	 */
	private String getSeverity(double sample_jump)
	{
		String severity = "Mild";
		if (sample_jump > 1.3 * edit_click_window_maximum_sample_jump)
			severity = "Moderate";
		if (sample_jump > 1.6 * edit_click_window_maximum_sample_jump)
			severity = "Severe";
		return severity;
	}
}