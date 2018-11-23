/**
 * ErrorChecker.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic.errorcheckers;

import jproductioncritic.ErrorReport;
import mckay.utilities.sound.sampled.AudioSamples;
import java.util.ArrayList;

/**
 * This abstract class serves as the superclass of algorithms that check for production errors. Note that
 * extending classes must implement a constructor, which must set all fields to default values, and which
 * must then replace some or all field values with any values that are specified in a ConfigurationSettings
 * object supplied as a parameter to the constructor. Note also that the  main processing work will be done in
 * the checkForErrors method.
 * 
 * @author Cory McKay
 */
public abstract class ErrorChecker
{
	/* FIELDS ***********************************************************************************************/

	
    /**
	 * The identifying name of this error checker.
	 */
	protected String	title;
	
	
	/**
	 * Whether or not the error type is instantaneous (only exists at a single time) or spans a range of 
	 * times. Is true in the former case and false in the latter case.
	 */
	protected boolean	is_instantaneous;

	
	/**
	 * The tag used in configurations settings to note whether or not this kind of error should be looked for
	 * during processing.
	 */
	protected String	should_check_tag;
	
	
	/**
	 * Whether or not this error should be looked for during processing under default settings.
	 */
	protected boolean	should_check_by_default;
		
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * All extending classes MUST have a constructor that sets ALL fields (inherited and not) to defaults and
	 * that has a single argument consisting of a ConfigurationSettings object. If this ConfigurationSettings 
	 * argument is non-null, then some or all defaults are replaced by the specified values.
	 */	
	
	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * Returns the title of this ErrorChecker.
	 * 
	 * @return	The identifying name of this error checker.
	 */
	public String getTitle()
	{
		return title;
	}
	
	
	/**
	 * Returns the preferences tag used to check whether or not this ErrorChecker should be applied.
	 * 
	 * @return	The tag used in configurations settings to note whether or not this kind of error should be
	 *			looked for during processing.
	 */
	public String getShouldExtractTag()
	{
		return should_check_tag;
	}
	
	
	/* PROTECTED METHODS ************************************************************************************/

	
	/**
	 * Returns the given ErrorReports joined into a single set of ErrorReports. If the reported errors are
	 * instantaneous, then reports that occur at the same instant are merged. If the reported errors span some
	 * time, then reports are merged if they overlap at all. Returned ErrorReports are all sorted by start
	 * time. Note that the original sets of error reports are not changed.
	 * 
	 * @param separated_reports	The ArrayList of ErrorReport objects to merge if appropriate. The array index
	 *							indicates the audio channel that the reports correspond to. The ArrayList for
	 *							each channel may not be null, but may be empty. This array must also not be
	 *							null.
	 * @return					A set of ErrorReports containing the reports in separated_reports merged
	 *							across channels.
	 */
	protected static ArrayList<ErrorReport> getErrorReportsMergedAcrossChannels(ArrayList<ErrorReport>[] separated_reports)
	{
		// The reports that will be returned
		ArrayList<ErrorReport> merged = new ArrayList<>();
		
		// Find the number of channels with errors
		int number_channels_with_errors = 0;
		for (int chan = 0; chan < separated_reports.length; chan++)
			if (!separated_reports[chan].isEmpty())
					number_channels_with_errors++;
		
		// Deal with the case where there are no error reports in any channels
		if (number_channels_with_errors == 0)
			return merged;
		
		// Deal with the case where there is only one channel with error reports
		if (number_channels_with_errors == 1)
		{
			for (int chan = 0; chan < separated_reports.length; chan++)
			{	
				if (!separated_reports[chan].isEmpty())
				{
					for (int rep = 0; rep < separated_reports[chan].size(); rep++)
						merged.add(separated_reports[chan].get(rep));
					return merged;
				}
			}
		}
		
		// The rest of this method deals with cases where there is more than one channel with errors

		// Generate interrepts, a version of separated_reports with channels with no error reports removed
		// where the first index is the channel and the second is the report index
		ErrorReport[][] interrepts = new ErrorReport[number_channels_with_errors][];
		int inter_chan = 0;
		for (int chan = 0; chan < separated_reports.length; chan++)
		{
			if (!separated_reports[chan].isEmpty())
			{
				interrepts[inter_chan] = new ErrorReport[separated_reports[chan].size()];
				for (int rep = 0; rep < separated_reports[chan].size(); rep++)
					interrepts[inter_chan][rep] = separated_reports[chan].get(rep);
				inter_chan++;
			}
		}
		
		// Sort all ErrorReports by start time
		ArrayList<ErrorReport> sorted_not_merged = new ArrayList<>();
		boolean found_one = true;
		while (found_one)
		{
			found_one = false;
			int first_start_time_so_far =-1;
			int first_so_far_i = -1;
			int first_so_far_j = -1;
			for (int i = 0; i < interrepts.length; i++)
				for (int j = 0; j < interrepts[i].length; j++)
					if (interrepts[i][j] != null)
					{
						if ( first_so_far_i == -1 || interrepts[i][j].start_time < first_start_time_so_far )
						{
							first_start_time_so_far = interrepts[i][j].start_time;
							first_so_far_i = i;
							first_so_far_j = j;
							found_one = true;
						}
					}
			if (found_one)
			{
				sorted_not_merged.add( interrepts[first_so_far_i][first_so_far_j] );
				interrepts[first_so_far_i][first_so_far_j] = null;
			}
		}
		
		// Merge all overlapping error reports if they are instantaneous
		if (sorted_not_merged.get(0).is_instantaneous)
		{
			// Perform the merging
			ErrorReport report_to_add = new ErrorReport ( sorted_not_merged.get(0).error_identifier,
														  sorted_not_merged.get(0).is_instantaneous,
														  sorted_not_merged.get(0).start_time,
													      sorted_not_merged.get(0).end_time,
													      sorted_not_merged.get(0).severity );
			for (int i = 0; i < sorted_not_merged.size(); i++)
			{
				if ( (sorted_not_merged.get(i).start_time == report_to_add.start_time) && i != 0 )
				{
					if (sorted_not_merged.get(i).severity.equals("Severe"))
						report_to_add.severity = "Severe";
					else if (sorted_not_merged.get(i).severity.equals("Moderate") && !report_to_add.severity.equals("Severe"))
						report_to_add.severity = "Moderate";
				}
				else if (i != 0)
				{
					merged.add(report_to_add);
					report_to_add = new ErrorReport ( sorted_not_merged.get(i).error_identifier,
													  sorted_not_merged.get(i).is_instantaneous,
													  sorted_not_merged.get(i).start_time,
													  sorted_not_merged.get(i).end_time,
													  sorted_not_merged.get(i).severity );
				}
			}
			merged.add(report_to_add);
		}
		
		// Merge all overlapping error reports if they are not instantaneous
		else if (!sorted_not_merged.get(0).is_instantaneous)
		{
			// Perform the merging
			ErrorReport report_to_add = new ErrorReport ( sorted_not_merged.get(0).error_identifier,
														  sorted_not_merged.get(0).is_instantaneous,
														  sorted_not_merged.get(0).start_time,
													      sorted_not_merged.get(0).end_time,
													      sorted_not_merged.get(0).severity );
			for (int i = 0; i < sorted_not_merged.size(); i++)
			{
				// Merge overlapping errors
				if ( (sorted_not_merged.get(i).start_time <= report_to_add.end_time) && i != 0 )
				{
					report_to_add.end_time = sorted_not_merged.get(i).end_time;
					if (sorted_not_merged.get(i).severity.equals("Severe"))
						report_to_add.severity = "Severe";
					else if (sorted_not_merged.get(i).severity.equals("Moderate") && !report_to_add.severity.equals("Severe"))
						report_to_add.severity = "Moderate";
				}

				
				// When a merge is not to be performed
				else if (i != 0)
				{
					merged.add(report_to_add);
					report_to_add = new ErrorReport ( sorted_not_merged.get(i).error_identifier,
													  sorted_not_merged.get(i).is_instantaneous,
													  sorted_not_merged.get(i).start_time,
													  sorted_not_merged.get(i).end_time,
													  sorted_not_merged.get(i).severity );
				}
			}
			merged.add(report_to_add);
		}
		
		// Return the results
		return merged;
	}

	
	/* ABSTRACT METHODS *************************************************************************************/

	
	/**
	 * Returns the default configuration settings associated with this ErrorChecker for use in creating a
	 * default configuration file. This includes the should_check_by_default field inherited from
	 * ErrorChecker, but not the title or is_instantaneous fields.
	 * 
	 * @return	An array of arrays where the first index corresponds to the individual setting. The second
	 *			index stores a given setting's configuration key in its 0th entry, and the setting's 
	 *			configuration value in its 1st entry. Returns null if there are no settings. Note that neither
	 *			keys nor values may include whitespace characters.
	 */
	public abstract String[][] getDefaultConfigurationSettings();	


	/**
	 * Verify that the configuration settings stored in the fields of this ErrorChecker are set to permitted 
	 * values, and add an informative report to the given list of problems if they are invalid. Do nothing if 
	 * the configuration settings stored in the fields are in fact valid.
	 * 
	 * @param problems	A list of configuration settings problems to add to if there are any problems with
	 *					the configuration settings values stored in the fields of this ErrorChecker.
	 */
	public abstract void validateConfigurationSettings(ArrayList<String> problems);
	

	/**
	 * Check the given audio for the technical recording or production error associated with this class and
	 * return any detected errors in the form of ErrorReports.
	 * 
	 * @param audio						The low-level audio data parsed from an audio file that is to be
	 *									checked. May not be null.  It is assumed that all channels in audio
	 *									contain an equal number of samples.
	 * @return							All detected errors, sorted by start time and all of the same type. An
	 *									empty ArrayList is returned if none are found. Will NOT be null.
     * @throws Exception				Throws an informative exception if error checking cannot be performed.
	 */
	public abstract ArrayList<ErrorReport> checkForErrors(AudioSamples audio)
		throws Exception;
}