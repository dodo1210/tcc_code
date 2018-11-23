/**
 * ErrorReport.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic;

import ace.datatypes.SegmentedClassification;
import mckay.utilities.general.HTMLWriter;
import mckay.utilities.staticlibraries.StringMethods;
import java.util.ArrayList;

/**
 * An object of this class stores the information associated with a particular error detected in an audio 
 * file. The fields of objects of this class store the basic information associated with the error. Methods 
 * are also included for merging error reports of the same type that are close in time and for formatting 
 * error reports for output in various ways.
 * 
 * @author	Cory McKay
 */
public class ErrorReport
{
	/* FIELDS ***********************************************************************************************/

    
	/**
	 * The name of the error type. May not be null.
	 */
	public String	error_identifier;
	
	/**
	 * Whether the error is instantaneous (only exists at a single time) or spans a range of times. Is true
	 * in the former case and false in the latter case.
	 */
	public boolean	is_instantaneous;
	
	/**
	 * The time in milliseconds at which the error exists (in the case of instantaneous errors) or the point 
     * in time at which the error begins (in the case of errors spanning a range of time). May not be less
     * than zero.
	 */
	public int		start_time;
	
	/**
	 * The time in milliseconds at which the error ends (in the case of errors spanning a range of time), or
     * -1 in the case of instantaneous errors. Should otherwise be larger than start_time.
	 */
	public int		end_time;
	
	/**
	 * An indication of the severity of the error. Is typically either "Mild", "Moderate" or "Severe". May be
	 * null if no severity indication is available.
	 */
	public String	severity;
    
    /**
     * The system-specific line separator.
     */
    private static final String ls = System.getProperty("line.separator");
	
    
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Create an error report consisting of the specified information.
	 * 
	 * @param error_identifier	The name of the error type. May not be null.
	 * @param is_instantaneous	Whether the error is instantaneous (only exists at a single time) or spans 
	 *							a range of times. Is true in the former case and false in the latter case.
	 * @param start_time		The time in milliseconds at which the error exists (in the case of 
     *                          instantaneous errors) or the point in time at which the error begins (in the 
     *                          case of errors spanning a range of time). May not be less than zero.
	 * @param end_time			The time in milliseconds at which the error ends (in the case of errors 
     *                          spanning a range of time), or -1 in the case of instantaneous errors.
	 * @param severity			An indication of the severity of the error. Is typically either "Mild", 
	 *							"Moderate" or "Severe". May be null if no severity indication is available.
	 */
	public ErrorReport ( String error_identifier,
						 boolean is_instantaneous,
						 int start_time,
						 int end_time,
						 String severity )
	{
		this.error_identifier = error_identifier;
		this.is_instantaneous = is_instantaneous;
		this.start_time = start_time;
		this.end_time = end_time;
		this.severity = severity;
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/


    /**
     * Returns the information stored in this error report as a string with one line for each field of the
     * object, followed by a single blank line.
     * 
     * @return  The information stored in this object.
     */
	public String getTextToAddToTextReport()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("ERROR TYPE: "); sb.append(error_identifier); sb.append(ls);
        if (is_instantaneous) {sb.append("TIME TYPE: Instantaneous"); sb.append(ls);}
        else {sb.append("TIME TYPE: Time Span"); sb.append(ls);}
        sb.append("START TIME: "); sb.append(StringMethods.getFormattedDuration(start_time)); sb.append(ls);
        if (!is_instantaneous) {sb.append("END TIME: "); sb.append(StringMethods.getFormattedDuration(end_time)); sb.append(ls);}
        sb.append("SEVERITY: "); sb.append(severity); sb.append(ls);
        sb.append(ls);
        
        return sb.toString();
	}
	
	/**
     * Returns the information stored in this error report as a string with one line for each field of the
     * object, followed by a single blank line. All of this is HTML-formatted.
     * 
     * @return  The information stored in this object.
     */
	public String getTextToAddToHtmlReport()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("<b>ERROR TYPE:</b> "); sb.append( HTMLWriter.convertSpecialCharacters(error_identifier) ); sb.append("<br>"); sb.append(ls);
        if (is_instantaneous) {sb.append("<b>TIME TYPE:</b> Instantaneous"); sb.append("<br>"); sb.append(ls);}
        else {sb.append("<b>TIME TYPE:</b> Time Span"); sb.append("<br>"); sb.append(ls);}
        sb.append("<b>START TIME:</b> "); sb.append(StringMethods.getFormattedDuration(start_time)); sb.append("<br>"); sb.append(ls);
        if (!is_instantaneous) {sb.append("<b>END TIME:</b> "); sb.append(StringMethods.getFormattedDuration(end_time)); sb.append("<br>"); sb.append(ls);}
        sb.append("<b>SEVERITY:</b> "); sb.append( HTMLWriter.convertSpecialCharacters(severity) ); sb.append("<br>"); sb.append(ls); sb.append("<br>"); sb.append(ls);
        
        return sb.toString();
	}
	
    /**
     * Returns the information stored in this error report as a string that represents one line of an 
	 * Audacity label track file. This consists of the start time of the error (in seconds), followed by a 
	 * tab, followed by the end time of the error (in seconds), followed by a tab, followed by a label
	 * indicating the error type and its severity.
     * 
     * @return  The information stored in this object.
     */
	public String getTextToAddToAudacityLabelTrack()
	{
		// Format the start and end times
		String form_start_time = Double.toString(((double) start_time) / 1000.00);
		String form_end_time = form_start_time;
		if (!is_instantaneous) form_end_time = Double.toString(((double) end_time) / 1000.00);
		
		// Prep the label
		String label = error_identifier + " (" + severity + ")";
		
		// Prepare the report line
		StringBuilder sb = new StringBuilder();
		sb.append(form_start_time);
		sb.append("\t");
		sb.append(form_end_time);
		sb.append("\t");
		sb.append(label);
        sb.append(ls);

		// Return the results
        return sb.toString();
	}

	/**
	 * Returns the information stored in this error report as an ACE SegmentedClassification object
	 * corresponding to a single SegmentedClassification sub-classification. No top level 
	 * SegmentedClassification is generated here. Start and end stamps are specified (in seconds), and two
	 * class labels are assigned: first the identifier for this error, and then its severity.
	 * 
	 * @return  The information stored in this object.
	 */
	public SegmentedClassification getContentToAddToAceXmlFile()
	{
		// Prep the start and end times
		double form_start_time = (((double) start_time) / 1000.00);
		double form_end_time = form_start_time;
		if (!is_instantaneous) form_end_time = (((double) end_time) / 1000.00);
		
		// Prep the class label
		String[] classifications = {error_identifier, severity};
		
		// Return the ACE SegMentedClassification sub-section
		return new SegmentedClassification( null,
											form_start_time,
											form_end_time,
											classifications,
											null,
											null,
											null );
	}

    /**
     * Returns the information stored in this error report as a string consisting of a single line
	 * corresponding to a single Weka ARFF instance. The comma-delimited attributes consist of start time (in
	 * seconds), end time (in seconds), severity and (as the class attribute) the error identifier (with 
	 * whitespace removed).
     * 
     * @return  The information stored in this object.
     */
	public String getTextToAddToWekaArffFile()
	{
		// Format the start and end times
		String form_start_time = Double.toString(((double) start_time) / 1000.00);
		String form_end_time = form_start_time;
		if (!is_instantaneous) form_end_time = Double.toString(((double) end_time) / 1000.00);
		
		// Add the attributes
		StringBuilder sb = new StringBuilder();
		sb.append(form_start_time); sb.append(",");
		sb.append(form_end_time); sb.append(",");
		sb.append(severity); sb.append(",");
		
		// Add the class attribute (with whitespace removed)
		sb.append(error_identifier.replaceAll("\\s","")); sb.append(ls);

		// Return the results
        return sb.toString();
	}
	
    
	/* PUBLIC STATIC METHODS ********************************************************************************/

    
   	/**
	 * Takes in a ArrayList of ErrorReport objects of the same type and merges those non-instantaneous reports 
     * that overlap with one another (in terms of the interval between the start_time and the end_time) as
     * well as any pairs of reports that are within proximity_threshold milliseconds of one another. A new
     * ArrayList is returned consisting of the results (even if no merges needed to be performed).
     *
	 * @param to_merge              The ArrayList of ErrorReport objects to merge if appropriate. It is 
     *                              assumed that the objects all have identical error_identifier fields, and 
     *                              that each ErrorReport object is listed sequentially from first to last 
     *                              based on its start_time field. The ArrayList may not be null, but it may  
     *                              be empty.
     * @param proximity_threshold   The maximum amount of time in milliseconds separating ErrorReport objects 
     *                              that should be merged. No merging will occur if this value is negative 
     *                              (except in the case of merging overlapping non-instantaneous reports,
     *                              which will always be merged).
	 * @return						A new ArrayList of new ErrorReport objects identical in content to that 
     *                              provided except that some ErrorReport objects may have been merged.
	 */
    public static ArrayList<ErrorReport> mergeErrorReports( ArrayList<ErrorReport> to_merge,
                                                            int proximity_threshold )
    {
		// The merged array that will be returned
		ArrayList<ErrorReport> merged = new ArrayList<>();
		
		// Simply return the orgiginal empty list if an empty list is provided
        if (to_merge.isEmpty()) return merged;
		
		// Deal with instantaneous error types
		if (to_merge.get(0).is_instantaneous)
		{
			// Simply return a copy if no merging is set to be performed
			if (proximity_threshold < 0)
			{
				for (int i = 0; i < to_merge.size(); i++)
					merged.add(to_merge.get(i));
				return merged;
			}
			
			// Perform the merging
			ErrorReport report_to_add = new ErrorReport ( to_merge.get(0).error_identifier,
														  to_merge.get(0).is_instantaneous,
														  to_merge.get(0).start_time,
													      to_merge.get(0).end_time,
													      to_merge.get(0).severity );
			int current_edge_time = to_merge.get(0).start_time;
			for (int i = 0; i < to_merge.size(); i++)
			{
				if ( (to_merge.get(i).start_time - current_edge_time) <= proximity_threshold && i != 0)
				{
					current_edge_time = to_merge.get(i).start_time;
					if (to_merge.get(i).severity.equals("Severe"))
						report_to_add.severity = "Severe";
					else if (to_merge.get(i).severity.equals("Moderate") && !report_to_add.severity.equals("Severe"))
						report_to_add.severity = "Moderate";
				}
				else if (i != 0)
				{
					merged.add(report_to_add);
					report_to_add = new ErrorReport ( to_merge.get(i).error_identifier,
													  to_merge.get(i).is_instantaneous,
													  to_merge.get(i).start_time,
													  to_merge.get(i).end_time,
													  to_merge.get(i).severity );
					current_edge_time = report_to_add.start_time;
				}
			}
			merged.add(report_to_add);
		}
			
		// Deal with time span error types
		else if (!to_merge.get(0).is_instantaneous)
		{
			// Perform the merging
			ErrorReport report_to_add = new ErrorReport ( to_merge.get(0).error_identifier,
														  to_merge.get(0).is_instantaneous,
														  to_merge.get(0).start_time,
													      to_merge.get(0).end_time,
													      to_merge.get(0).severity );
			for (int i = 0; i < to_merge.size(); i++)
			{
				// Merge overlapping errors
				if ( (to_merge.get(i).start_time <= report_to_add.end_time) && i != 0 )
				{
					report_to_add.end_time = to_merge.get(i).end_time;
					if (to_merge.get(i).severity.equals("Severe"))
						report_to_add.severity = "Severe";
					else if (to_merge.get(i).severity.equals("Moderate") && !report_to_add.severity.equals("Severe"))
						report_to_add.severity = "Moderate";
				}
				
				// Merge when ending of one error is close to start of next error
				else if ( proximity_threshold >= 0 &&
					 i != 0 &&
					 (to_merge.get(i).start_time - report_to_add.end_time) <= proximity_threshold	)
				{
					report_to_add.end_time = to_merge.get(i).end_time;
					if (to_merge.get(i).severity.equals("Severe"))
						report_to_add.severity = "Severe";
					else if (to_merge.get(i).severity.equals("Moderate") && !report_to_add.severity.equals("Severe"))
						report_to_add.severity = "Moderate";
				}
				
				// When a merge is not to be performed
				else if (i != 0)
				{
					merged.add(report_to_add);
					report_to_add = new ErrorReport ( to_merge.get(i).error_identifier,
													  to_merge.get(i).is_instantaneous,
													  to_merge.get(i).start_time,
													  to_merge.get(i).end_time,
													  to_merge.get(i).severity );
				}
			}
			merged.add(report_to_add);	
		}		
		
		// Return the results
		return merged;
    }
}