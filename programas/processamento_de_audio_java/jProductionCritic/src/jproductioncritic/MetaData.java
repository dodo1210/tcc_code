/**
 * MetaData.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic;

import mckay.utilities.general.HTMLWriter;

/**
 * Objects of this class store pairs of strings: one indicating the identifier for a piece of metadata, and
 * the other indicating the value for this piece of metadata. Any kind of metadata acquired during
 * jProductionCritic processing can be stored in objects of this type, other than reports of detected
 * errors, which should rather be stored in ErrorReport objects. Typically, MetaData objects store metadata
 * about the audio files that are checked for errors. Methods are also included for formatting error reports
 * in various ways.
 
 * @author	Cory McKay
 */
public class MetaData
{
	/* FIELDS ***********************************************************************************************/

    
	/**
	 * The field name for this kind of metadata. May not be null.
	 */
	public String	identifier;

	/**
	 * The field name for this kind of metadata. May be null in order to indicate that no value is known.
	 */
	public String	value;
    
    /**
     * The system-specific line separator.
     */
    private static final String ls = System.getProperty("line.separator");
	
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Create a MetaData object storing the specified information.
	 * 
	 * @param identifier	The field name for this kind of metadata. May not be null.
	 * @param value			The field name for this kind of metadata. May be null in order to indicate that no
	 *						value is known.
	 */
	public MetaData ( String identifier,
					  String value )
	{
		this.identifier = identifier;
		this.value = value;
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/


    /**
     * Returns the information stored in this object as the identifier, followed by a colon a space, followed
	 * by the value and a line break.
     * 
     * @return  The information stored in this object.
     */
	public String getTextToAddToTextReport()
	{
		StringBuilder sb = new StringBuilder();
        sb.append(identifier); sb.append(": ");
        sb.append(value); sb.append(ls);

        return sb.toString();
	}
	
	/**
     * Returns the information stored in this object as the identifier, followed by a colon a space, followed
	 * by the value and a line break, all in an HTML-formatted format.
     * 
     * @return  The information stored in this object.
     */
	public String getTextToAddToHtmlReport()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("<b>"); sb.append( HTMLWriter.convertSpecialCharacters(identifier) ); sb.append(":</b> ");
        sb.append( HTMLWriter.convertSpecialCharacters(value) ); sb.append("<br>"); sb.append(ls);

        return sb.toString();
	}
	
	/**
	 * Returns the information stored in this object as a String array of size two, containing first the 
	 * identifier field value of this object, followed by its value field value.
	 * 
	 * @return	The information stored in this object.
	 */
	public String[] getContentToAceXmlFile()
	{
		String[] metadata = {identifier, value};
		return metadata;
	}
}