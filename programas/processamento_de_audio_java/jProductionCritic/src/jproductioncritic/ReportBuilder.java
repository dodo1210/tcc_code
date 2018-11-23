/**
 * ReportBuilder.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic;

import jproductioncritic.errorcheckers.ErrorChecker;
import ace.datatypes.SegmentedClassification;
import mckay.utilities.general.HTMLWriter;
import java.util.ArrayList;

/**
 * A class consisting of static methods for generating various types of reports detailing the contents of sets
 * of ErrorReport and MetaData objects.
 * 
 * @author Cory McKay
 */
public class ReportBuilder
{
	/* FIELDS ***********************************************************************************************/

    
    /**
     * The system-specific line separator.
     */
    private static final String ls = System.getProperty("line.separator");
	
    
    /* PUBLIC STATIC METHODS ********************************************************************************/

    
    /**
     * Returns a string containing a labeled text report detailing the contents of the specified metadata and
	 * detected errors for a single analyzed file. The metadata is listed first, with one line per 
	 * identifier/value pair followed by a blank line. The total number of detected errors is then listed. 
	 * Each error is then listed as a separate paragraph, and each piece of data about an error is listed on a
	 * separate line. No merging of reports is performed by this method. 
     * 
     * @param   metadata	The metadata to detail in the returned report. May not be null, but may be of size
	 *						0 if there is no metadata to report.
     * @param   errors		The detected errors to detail in the returned report. May not be null, but may be
	 *						of size 0 if there are no errors to report.
     * @return				The complete error report.
     */
    public static String generateTextReport( ArrayList<MetaData> metadata,
											 ArrayList<ErrorReport> errors )
    {
        // To store the full report
        StringBuilder fr = new StringBuilder();

		// Add the metadata
        for (int i = 0; i < metadata.size(); i++)
            fr.append(metadata.get(i).getTextToAddToTextReport());
		if (!metadata.isEmpty())
			fr.append(ls);
		
        // Generate the error header
        fr.append("TOTAL NUMBER OF ERRORS DETECTED: "); fr.append(errors.size()); fr.append(ls); fr.append(ls);
        
        // Add the details of each error
        for (int i = 0; i < errors.size(); i++)
        {
            fr.append("ERROR NUMBER: "); fr.append( i+1 ); fr.append(ls);
            fr.append(errors.get(i).getTextToAddToTextReport());
        }
        
        // Return the results
		return fr.toString();
    }
	
	/**
     * Returns a string containing a labeled HTML report detailing the contents of the specified metadata and
	 * detected errors for a single analyzed file. The metadata is listed first, with one line per 
	 * identifier/value pair. The total number of detected errors is then listed. Each error is then listed as
	 * a separate paragraph, and each piece of data about an error is listed on a separate line. No merging of 
	 * reports is performed by this method. 
     * 
     * @param   metadata	The metadata to detail in the returned report. May not be null, but may be of size
	 *						0 if there is no metadata to report.
     * @param   errors		The detected errors to detail in the returned report. May not be null, but may be
	 *						of size 0 if there are no errors to report.
     * @return				The complete error report.
     */
	public static String generateHtmlReport( ArrayList<MetaData> metadata,
											 ArrayList<ErrorReport> errors )
	{
		// Initialize the HTML file
		StringBuffer html = HTMLWriter.startNewHTMLFile( "#FFFFFF",
				                                         "#000000",
														 "jProduction Analysis Results",
														 false );
		html.append(ls);
		
		// Add the metadata
        html.append("<font color=\"#0000FF\"><b>FILE METADATA:</b></font><br>"); html.append(ls); html.append("<br>"); html.append(ls);
        for (int i = 0; i < metadata.size(); i++)
            html.append(metadata.get(i).getTextToAddToHtmlReport());
		if (!metadata.isEmpty())
			{html.append("<br>"); html.append(ls);}
		
        // Generate the error header
        html.append("<b><font color=\"#0000FF\">TOTAL NUMBER OF ERRORS DETECTED:</font><font color=\"#FF0000\"> "); html.append(errors.size()); html.append("</font></b><br>"); html.append(ls); html.append("<br>"); html.append(ls);

        // Add the details of each error
        for (int i = 0; i < errors.size(); i++)
        {
            html.append("<b>ERROR NUMBER:</b> "); html.append( i+1 ); html.append("<br>"); html.append(ls);
            html.append(errors.get(i).getTextToAddToHtmlReport());
        }
		
		// End the HTML file
		HTMLWriter.endHTMLFile(html, false);
		
		// Return the results
		return html.toString();
	}

	/**
     * Returns a string containing the contents of an Audacity label file detailing the detected errors for a
	 * single analyzed audio file. This Audacity label file includes a label for each error detected. Each 
	 * line of this file (which is essentially a text file) contains a single line for each label, and each
	 * such line consists of the start time of the error (in seconds), followed by a tab, followed by
	 * the end time of the error (in seconds), followed by a tab, followed by a label indicating the error
	 * type and its severity.
	 * 
     * @param   errors		The detected errors to include as labels in the returned report. May not be null,
	 *						but may be of size 0 if there are no errors to report.
     * @return				Information to save in Audacity label tracks. Null is returned if no errors were
	 *						found.
	 */
	public static String generateAudacityLabelTrack(ArrayList<ErrorReport> errors)
	{
		// The contents of the Audacity label file to be generated
		StringBuilder lf = new StringBuilder();
		
		// Fill the Audacity label file
        for (int i = 0; i < errors.size(); i++)
			lf.append( errors.get(i).getTextToAddToAudacityLabelTrack() );

		// Return the results
		if (errors.isEmpty()) return null;
		else return lf.toString();
	}
	
	/**
     * Returns an ACE SegmentedClassification object corresponding to a single instance, namely the complete
	 * results of processing a single audio file. Each detected error is included as a single
	 * SegmentedClassification sub-classification, with its start and end times specified (in seconds), along
	 * with two class labels: first the identifier for the error, and then its severity. All available
	 * metadata is also included for the top-level SegmentedClassification object. The top-level
	 * SegmentedClassification object is assigned an identifier corresponding to the audio file's path, and
	 * no top-level class labels are assigned.
	 * 
     * @param   metadata	The metadata to detail in the returned report. May not be null, but may be of size
	 *						0 if there is no metadata to report.
     * @param   errors		The detected errors to detail in the returned report. May not be null, but may be
	 *						of size 0 if there are no errors to report.
     * @return				The complete ACE SegmentedClassification object for the audio file data passed to
	 *						this method.
	 */
	public static SegmentedClassification generateAceXmlClassification( ArrayList<MetaData> metadata,
				       					                                ArrayList<ErrorReport> errors )
	{
		// Find the file path of the audio file that was analyzed
		String file_path = "";
		for (int i = 0; i < metadata.size(); i++)
			if (metadata.get(i).identifier.equals("FILE PATH"))
				file_path = metadata.get(i).value;
		
		// Prepare the metadata reports
		String[] metadata_identifiers = new String[metadata.size()];
		String[] metadata_values = new String[metadata.size()];
		for (int i = 0; i < metadata.size(); i++)
		{
			String[] this_metadata = metadata.get(i).getContentToAceXmlFile();
			metadata_identifiers[i] = this_metadata[0];
			metadata_values[i] = this_metadata[1];
		}
		
		// Prepare the error reports
		SegmentedClassification[] sub_classifications = null;
		if (!errors.isEmpty())
		{
			sub_classifications = new SegmentedClassification[errors.size()];
			for (int i = 0; i < errors.size(); i++)
				sub_classifications[i] = errors.get(i).getContentToAddToAceXmlFile();
		}

		// Return the results
		return new SegmentedClassification( file_path,
											Double.NaN,
											Double.NaN,
											null,
											metadata_values,
											metadata_identifiers,
											sub_classifications );		
	}

	/**
     * Returns a string containing the contents of a Weka ARFF file detailing the detected errors for a
	 * single analyzed audio file. The attributes for each instance are the start time (in seconds), end time
	 * (in seconds), severity and (as the class attribute) the error identifier (with whitespace removed). The
	 * candidate classes are all of the error types checked for (with whitespace removed). Note that this is
	 * an unusual type of data assignment to use in a Weka ARFF file, but is the best way to preserve the
	 * information output by jProductionCritic given the limitations of the ARFF format.
	 * 
     * @param   errors					The detected errors to include as labels in the returned report. May
	 *									not be null, but may be of size 0 if there are no errors to report.
	 * @param error_checkers_applied	One instance of each ErrorChecker algorithm chosen that was applied 
	 *									during error checking. May not be null.
     * @return							Information to save in a Weka ARFF file.
	 */
	public static String generateWekaArffFile( ArrayList<ErrorReport> errors,
											   ArrayList<ErrorChecker> error_checkers_applied )
	{
		// To store the ARFF file
		StringBuilder af = new StringBuilder();
		
		// Prepare the set of classes (i.e. errors checked for)
		StringBuilder classes = new StringBuilder();
		classes.append("{");
		for (int i = 0; i < error_checkers_applied.size(); i++)
		{
			// Remove whitespace
			String formatted_class = error_checkers_applied.get(i).getTitle().replaceAll("\\s","");
			
			// Add the formatted class
			classes.append( formatted_class );
			if (i != error_checkers_applied.size() - 1)
				classes.append(",");
		}
		classes.append("}");
		
		// Generate the ARFF header
		af.append("@RELATION jProductionCritic"); af.append(ls); af.append(ls);
		af.append("@ATTRIBUTE starttime\tNUMERIC"); af.append(ls);
		af.append("@ATTRIBUTE endtime\tNUMERIC"); af.append(ls);
		af.append("@ATTRIBUTE severity\t{Mild,Moderate,Severe}"); af.append(ls);
		af.append("@ATTRIBUTE class\t"); af.append(classes.toString()); af.append(ls); af.append(ls);
		
		// Generate the ARFF data
		af.append("@DATA"); af.append(ls);
		for (int i = 0; i < errors.size(); i++)
			af.append( errors.get(i).getTextToAddToWekaArffFile());
		
		// Return the results
		return af.toString();
	}
}