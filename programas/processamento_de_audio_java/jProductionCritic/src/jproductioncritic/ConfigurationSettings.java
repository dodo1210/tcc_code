/**
 * ConfigurationSettings.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package jproductioncritic;

import jproductioncritic.errorcheckers.ErrorChecker;
import mckay.utilities.staticlibraries.FileMethods;
import mckay.utilities.staticlibraries.SortingMethods;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import mckay.utilities.staticlibraries.StringMethods;

/**
 * An object of this class holds the configuration settings used for jProductionCritic processing, which are
 * typically parsed from a jProductionCritic configuration file. This class also includes methods for 
 * parsing such files, for saving such files, for preparing default settings and for validating settings.
 * 
 * @author Cory McKay
 */
public class ConfigurationSettings
{
	/* FIELDS ***********************************************************************************************/

    
	/**
	 * The configurations for running jProductionCritic processing. Each configuration setting has a value
	 * that is mapped to by its corresponding configuration key. Note that neither keys nor values may include
	 * whitespace characters.
	 */
	private HashMap<String, String> configuration_settings;
	
    
	/* CONSTRUCTORS *****************************************************************************************/

	
	/**
	 * Instantiate a new ConfigurationSettings object based on the settings specified in the given 
	 * jProductionCritic configurations file. If the config_file_path parameter is null or if the file
	 * cannot be parsed, then default field values are used and a new config file based on these defaults is
	 * saved to config_file_path, overwriting anything already there. Even if parsing does occur successfully,
	 * if an important setting is missing individually from the configuration file, then a default value for
	 * it will be used during processing.
	 *
	 * @param config_file_path	The path of the configurations file to parse settings from. May be null, in 
	 *							which case default settings are used.
	 * @param error_checkers	All valid ErrorChecker objects, from which default configuration settings can
	 *							be taken. May not be null. None may have identical configuration keys to one
	 *							another (will overwrite if they do). All ErrorCheckers should be included,
	 *							whether or not they are to be used by default. Note that neither keys nor
	 *							values may include tab characters. Only used if default settings need
	 *							to be generated.
	 * @param warning_stream	The stream to write warning messages to if non-critical problems occur. There
	 *							will be no feedback provided if this is null. Note that no testing is
	 *							performed here to see if parsed settings fall within specified ranges.
	 */
	public ConfigurationSettings( String config_file_path,
								  ArrayList<ErrorChecker> error_checkers,
								  PrintStream warning_stream )
	{
		// Set configuration settings to defaults
		setConfigurationsToDefaults(error_checkers);
		
		// Parse configuration settings from the configuration file, thereby erasing defaults. Restore
		// defaults and save them if parsing does not work.
		if (config_file_path != null)
		{
			try { parseConfigurationFile(config_file_path, warning_stream); }
			catch (Exception e)
			{
				if (warning_stream != null)
				{
					warning_stream.println("WARNING: Could not parse the jProductionCritic configuration file that should be at " + config_file_path);
					warning_stream.println("DETAILS: " + e.getMessage());
					warning_stream.println("This is normal the first time that this program is run.\n");
					warning_stream.println("Reverting to default configuration settings...\n");
					warning_stream.println("Saving default configuration settings to " + config_file_path + "...\n");
					warning_stream.flush();
				}

				// If loading did not work, revert to default settings and save them
				try
				{
					setConfigurationsToDefaults(error_checkers);
					saveConfigurationFile(config_file_path);
					if (warning_stream != null)
					{
						warning_stream.println("Saving of configuration settings complete.");
						warning_stream.println("It is suggested that you check these settings now that they are saved, and change them if you wish by editing the file manually.\n");
						warning_stream.flush();
					}
				}
				catch (Exception f)
				{
					if (warning_stream != null)
					{
						warning_stream.println("WARNING: Could not save the jProductionCritic file to " + config_file_path);
						warning_stream.println("DETAILS: " + f.getMessage());
						warning_stream.println("Please be sure that this directory exists with read/write permissions, otherwise it will not be possible to save a configuration file.");
						warning_stream.println("Proceeding with default (unsaved) configuration settings...\n");
						warning_stream.flush();
					}
				}
			}
		}
	}	
	
	
	/**
	 * Instantiate a new ConfigurationSettings object based on the settings specified in the given String
	 * array. All settings referred to in error_checkers are first initialized to default values, and only
	 * then are the values in new_settings added (possibly overwriting default values). Note that no 
	 * validation is performed here.
	 *
	 * @param new_settings		New configuration settings to load. The first index indicates the particular
	 *							setting. The second index, which must be of size 2, indicates (in 0) the
	 *							identifier of the setting and (in 1) the value of the setting. This may not
	 *							be null.
	 * @param error_checkers	All valid ErrorChecker objects, from which default configuration settings can
	 *							be taken. May not be null. None may have identical configuration keys to one
	 *							another (will overwrite if they do). All ErrorCheckers should be included,
	 *							whether or not they are to be used by default. Note that neither keys nor
	 *							values may include tab characters.
	 */
	public ConfigurationSettings( String[][] new_settings,
								  ArrayList<ErrorChecker> error_checkers )
	{
		// Set configuration settings to defaults
		setConfigurationsToDefaults(error_checkers);
		
		// Add the specified settings to defaults
		for (int i = 0; i < new_settings.length; i++)
			addConfiguration(new_settings[i][0], new_settings[i][1]);
	}

	
	/* STATIC PUBLIC METHODS ********************************************************************************/

	
	/**
	 * Verify that the given ErrorCheckers each have their fields set to values that are valid (needs to be
	 * done because the user may have inadvertently have caused the fields to have invalid values due to 
	 * invalid settings entered in the configuration settings file).
	 * 
	 * @param error_checkers	The list of ErrorChecker objects to check.
	 * @return					A formatted description of problems detected in the configuration settings.
	 *							Null is returned if no problems were found.
	 */
	public static String validateConfigurationSettings(ArrayList<ErrorChecker> error_checkers)
	{
		// A list to hold configuration settings problems
		ArrayList<String> config_settings_problems = new ArrayList<>();
		
		// Find any problems with the configuration settings stored in each of the given error checkers
		if (!error_checkers.isEmpty())
			for (int i = 0; i < error_checkers.size(); i++)
				error_checkers.get(i).validateConfigurationSettings(config_settings_problems);
		
		// Format the set of problems, if any
		String results = null;
		if (!config_settings_problems.isEmpty())
		{
			results = "Invalid configuration settings specified:\n";
			for (int i = 0; i < config_settings_problems.size(); i ++)
				results += "     - " + config_settings_problems.get(i) + "\n";
		}		
		
		// Return the results
		return results;
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * Returns the configuration value for the given configuration key.
	 * 
	 * @param config_key	The configuration key.
	 * @return				The configuration value, or null if there is no mapping for config_key or if the
	 *						value is itself null.
	 */
	public String getConfigurationSetting(String config_key)
	{
		return configuration_settings.get(config_key);
	}
	

	/**
	 * Print out all currently stored configuration settings to standard out, with one line for each setting,
	 * and each line consisting of the setting key, a tab and the setting value.
	 */
	public void printSettingsToStandardOut()
	{
		// Get all of the keys stored in configuration_settings
		Set<String> keys_set = configuration_settings.keySet();
		String[] keys = keys_set.toArray(new String[1]);
		
		// Sort the keys
		String[] sorted_keys = SortingMethods.sortArray(keys);
		
		// Print keys and values to standard out
		for (int i = 0; i < sorted_keys.length; i++)
			System.out.println(sorted_keys[i] + "\t" + configuration_settings.get(sorted_keys[i]));
	}
	
	
	/**
	 * Return all currently stored configuration settings.
	 * 
	 * @return	An array of configuration settings. The first index indicates the particular setting. The
	 *			first value of the second index indicates the setting name, and the second value indicates 
	 *			that setting's value.
	 */
	public String[][] getStructuredConfigurationSettings()
	{
		// Get all of the keys stored in configuration_settings
		Set<String> keys_set = configuration_settings.keySet();
		String[] keys = keys_set.toArray(new String[1]);
		
		// Sort the keys
		String[] sorted_keys = SortingMethods.sortArray(keys);
		
		// Organize keys and values to return
		String[][] to_return = new String[sorted_keys.length][2];
		for (int i = 0; i < to_return.length; i++)
		{
			to_return[i][0] = sorted_keys[i];
			to_return[i][1] = configuration_settings.get(sorted_keys[i]);
		}
		
		// Return the results
		return to_return;
	}
	
	
	/**
	 * Save the configuration settings stored in this object to a jProductionCritic file at the specified 
	 * path. Consists of one line for each setting, with each line consisting of the setting's key, followed
	 * by a tab, followed by the setting's value.
	 *
	 * @param save_path		The file path to save the configuration file to.
	 * @throws Exception	An exception is thrown if the save could not be performed.
	 */
	public final void saveConfigurationFile(String save_path)
		throws Exception
	{
		// Get all of the keys stored in configuration_settings
		Set<String> keys_set = configuration_settings.keySet();
		String[] keys = keys_set.toArray(new String[1]);
		
		// Sort the keys
		String[] sorted_keys = SortingMethods.sortArray(keys);
		
		// Save the keys and their associated values one by one and then flush and close the writer
		try
		{
			try (FileWriter writer = new FileWriter(new File (save_path)))
			{
				for (int i = 0; i < sorted_keys.length; i++)
					writer.write(sorted_keys[i] + "\t" + configuration_settings.get(sorted_keys[i]) + "\n");
				writer.flush();			
			}
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			throw new Exception("Given file " + save_path + " may not be written to. Details: " + e.getMessage());
		}
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Set all configuration settings in this object to their defaults, erasing all existing configuration
	 * settings, if any. These defaults may be specified in this object or in associated ErrorChecker classes.
	 * 
	 * @param error_checkers	All valid ErrorChecker objects, from which default configuration settings can
	 *							be taken. May not be null. None may have identical configuration keys to one
	 *							another (will overwrite if they do). All ErrorCheckers should be included,
	 *							whether or not they are to be used by default. Note that neither keys nor
	 *							values may include tabs characters.
	 */
	private void setConfigurationsToDefaults(ArrayList<ErrorChecker> error_checkers)
	{
		// Prepare a new set of configurations, erasing any that may already be there
		configuration_settings = new HashMap<>();
		
		// Add a default global preference indicating the maximum amount of time in milliseconds separating
		// detected errors of the same kind whose reports should be merged. No merging will occur if the time
		// separation between errors is greater than this, or if this value is negative (except in the case of 
		// merging overlapping non-instantaneous reports, which will always be merged).
		addConfiguration("error_report_merge_proximity_maximum", "2500");
		
		// Add a default global preference indicating the analysis window size (in samples) to use when
		// performing short analyses in the course of looking for various kinds of errors. This window
		// size may be used by individual error checkers unless they have a particular setting of their own
		// to override it. This value must be greater than 1, and it is advisable that it be a power or 2
		// for the purpose of calculation efficiency.
		addConfiguration("analysis_window_size", "512");
		
		// Add a default global preference indicating the analysis window size (in samples) to use when
		// performing analyses over very long windows (usually more than a second) in the course of
		// looking for various kinds of errors. This window size may be used by individual error checkers 
		// unless they have a particular setting of their own to override it. This value must be greater than
		// 1, and it is advisable that it be a power or 2 for the purpose of calculation efficiency.
		addConfiguration("long_window_size", "262144");
				
		// Iterate through each of the ErrorChecker defaults, and add them one by one
		for (int i = 0; i < error_checkers.size(); i++)
		{
			String[][] this_error_checker_settings = error_checkers.get(i).getDefaultConfigurationSettings();
			if (this_error_checker_settings != null)
			{
				for (int j = 0; j < this_error_checker_settings.length; j++)
					addConfiguration(this_error_checker_settings[j][0], this_error_checker_settings[j][1]);
			}
		}
	}
		
	
	/**
	 * Parse the contents of a jProductionCrigic configuration file and store its contents in this object.
	 * Existing settings are maintained, but any existing settings that share a key with the parsed results
	 * will be set to the value from the parsed file. 
	 *
	 * @param path_of_file_to_parse	The path of the configurations file to parse.
	 * @param warning_stream		The stream to write warning messages to if problems occur. There will be 
	 *								no feedback provided if this is null.
	 * @throws Exception			An informative exception is thrown if an invalid file path or an 
	 *								invalid file is given to this method.
	 */
	private void parseConfigurationFile( String parse_path,
			                             PrintStream warning_stream )
		throws Exception
	{
		// Parse the configurations file into lines
		String[] lines = FileMethods.parseTextFileLines(new File(parse_path));
		
		// Parse each line of the configurations file and store the results (reporting problems as they
		// occur).
		for (int i = 0; i < lines.length; i++)
		{
			String results[] = StringMethods.breakIntoTokens(lines[i], "\t");
			if (results.length == 2)
				addConfiguration(results[0], results[1]);
			else if (warning_stream != null)
			{
				if (results.length == 0)
					warning_stream.println("Specified configurations file has an empty entry");
				else	
					warning_stream.println("Specified configurations file has an invalid entry: " + results[0]);
				warning_stream.println("Proceeding to parse other configuration settings configuration settings...\n");
				warning_stream.flush();
			}	
		}
	}
	
    
	/**
	 * Store the given configuration value under the given configuration key, replacing any value that may
	 * or may not have been there for the given key.
	 * 
	 * @param conig_key		The configuration key.
	 * @param conig_value	The configuration value.
	 */
	private void addConfiguration(String conig_key, String config_value)
	{
		configuration_settings.put(conig_key, config_value);
	}
}