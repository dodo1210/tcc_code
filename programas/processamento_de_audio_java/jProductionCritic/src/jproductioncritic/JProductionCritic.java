/**
 * JProductionCritic.java
 * Version 1.2.2
 *
 * Last modified on May 5, 2017.
 * Marianopolis College and CIRMMT
 */

package jproductioncritic;

import jproductioncritic.errorcheckers.*;
import ace.datatypes.SegmentedClassification;
import mckay.utilities.sound.sampled.AudioSamples;
import mckay.utilities.staticlibraries.FileMethods;
import mckay.utilities.staticlibraries.MiscellaneousMethods;
import mckay.utilities.staticlibraries.StringMethods;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

/**
 * <p>jProductionCritic is a piece of software for automatically detecting recording and production technical
 * errors in sound files.</p>
 * 
 * <p>This is the core class for the jProductionCritic project. It contains jProductionCritic's main method,
 * as well as methods implementing jProductionCritic's top-level processing functionality.</p>
 * 
 * @author	Cory McKay
 */
public class JProductionCritic
{
	/* MAIN METHOD ******************************************************************************************/


	/**
	 * Parses command line arguments. Runs the GUI if there are no command line arguments, otherwise causes
	 * processing to occur corresponding to the contents of the command line arguments.
	 *
	 * @param args	The command line arguments.
	 */
	public static void main(String[] args)
	{
		// Run the GUI
		if (args.length == 0) gui.OuterFrame.startGUI();
			
		// Run command line processing
		else performAllProcessingBasedOnCommandLineArguements( args, System.out, System.err);
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * Perform all jProductionCritic processing based on the given command line arguments (whether they are
	 * actually from the command line or have been prepared elsewhere).
	 * 
	 * @param command_args	The given command line arguments (whether they are actually from the command line
	 *						or have been prepared elsewhere).
	 * @param status_stream A stream to print processing status updates. May also be used to write reports to
	 *						if this option is specified in command_args. May not be null.
	 * @param error_stream	The stream to write warning or error messages to if problems occur during 
	 *						processing. May not be null.
	 */
	public static void performAllProcessingBasedOnCommandLineArguements( String[] command_args,
											                             PrintStream status_stream,
											                             PrintStream error_stream )
	{
		try
		{
			// Parse and perform preliminary validation on command line arguments
			HashMap<String, String> execution_settings = parseAndTestCommandLineArguments ( command_args,
																						    error_stream );

			// Load the configurations file, creating a default one if a valid one is not at the path
			// specified. If no configuration settings file path is given, it is defaulted to
			// ./jProductionCriticConfigs.jpc
			status_stream.println(">>> Examining configuration settings . . .\n");
			String configuration_file_path = execution_settings.get("-configfile");
			if (configuration_file_path == null) configuration_file_path = "./jProductionCriticConfigs.jpc";
			ConfigurationSettings preferences = new ConfigurationSettings( configuration_file_path,
																		   getAllErrorCheckers(null),
																		   error_stream );
			// preferences.printSettingsToStandardOut();

			// Prepare the error checking alogrithms chosen to be applied in the preferences, and configure
			// them with the options set in the preferences. Also check validity of preferences.
			ArrayList<ErrorChecker> error_checkers_to_apply = getChosenErrorCheckers(preferences);
			// for (int i = 0; i < error_checkers_to_apply.size(); i++) status_stream.println(error_checkers_to_apply.get(i).getTitle());

			// Acquire and perform preliminary validation of the files to process
			status_stream.println(">>> Acquiring files to check for errors . . .\n");
			ArrayList<File> all_files = getFilesToCheck(execution_settings, error_stream);
			// for (int i = 0; i < files.size(); i++) status_stream.println(files.get(i).getAbsolutePath());

			// Prepare to store the metadata and error reports for each file that is processed. The outer
			// index of each indicates the file, and the inner index index indicates the piece of information.
			// Note that the outer index of each must match the outer index of the other. Neither may be null
			// or contain nulls.
			ArrayList<ArrayList<MetaData>> combined_metadata = new ArrayList<>();
			ArrayList<ArrayList<ErrorReport>> combined_error_reports = new ArrayList<>();
		
			// Open files one by one and test for errors and store metadata for each
			status_stream.println(">>> Processing files . . .\n");
			for (int i = 0; i < all_files.size(); i++)
			{
				status_stream.println("\t>>> Opening and checking " + all_files.get(i).getName() + "  . . .");
				try
				{
					int errors_found = processFile( all_files.get(i),
								                    combined_metadata, 
								                    combined_error_reports,
													error_checkers_to_apply,
													preferences,
								                    status_stream,
													error_stream );
					status_stream.println("\t>>> Total errors found in " + all_files.get(i).getName() + 
							              ": " + errors_found + "\n");
				}
				catch (Exception e) 
				{
					error_stream.println("Error processing " + all_files.get(i).getName() + ": " + e.getMessage());
					// e.printStackTrace();
				}
			}
			
			// Note if no errors were found
			if (combined_error_reports.isEmpty())
				status_stream.println("\n>>> No errors were detected.\n");
			
			// Prepare and output the error reports
			status_stream.println(">>> Preparing and outputting reports . . .\n");
			outputReports( combined_metadata, 
					       combined_error_reports,
						   error_checkers_to_apply,
						   execution_settings,
						   status_stream,
						   error_stream );

			// Finalize execution
			status_stream.println(">>> Processing complete.\n");
		}
		catch (Throwable t)
		{
			// Flush print streams
			status_stream.flush();
			error_stream.flush();
			
			// Print a preparatory error message
			error_stream.println("\nFATAL PROMBLEM DETECTED:\n");

			// React to the Java Runtime running out of memory
			if (t.toString().startsWith("java.lang.OutOfMemoryError"))
			{
				error_stream.println("- The Java Runtime ran out of memory.");
				error_stream.println("- Rerun this program with more more assigned to the runtime heap.");
			}
			else if (t instanceof Exception)
			{
				Exception e = (Exception) t;
				error_stream.println("- " + e.getMessage());
				// e.printStackTrace(error_stream);
			}

			// Print final error message
			error_stream.println("\nJPRODUCTIONCRITIC PROCESSING TERMINATED AFTER FATAL ERROR. PROCESSING INCOMPLETE.");
			
			// End execution
			System.exit(0);
		}
	}
	
	
	/**
	 * Return a list containing one instance of each implemented ErrorChecker sub-class. All ErrorCheckers
	 * are returned, even if they are not to be used for processing (the decision of whether or not to use
	 * a given ErrorChecker is made based on configuration file settings). This method also verifies that
	 * the settings stored in the preferences argument are valid.
	 * 
	 * @param preferences	The execution settings indicating, among other things, whether or not each
	 *						ErrorChecker algorithm is to be applied. May be null, in which case default
	 *						preferences will be auto-generated for each ErrorChecker.
	 * @return				A list containing one instance of each implemented ErrorChecker sub-class.
     * @throws Exception	Throws an informative exception if one or more of the ErrorCheckers are assigned
	 *						invalid configuration settings in the specified preferences.
	 */
	public static ArrayList<ErrorChecker> getAllErrorCheckers( ConfigurationSettings preferences )
		throws Exception
	{
		// Add all error checkers
		ArrayList<ErrorChecker> all_error_checkers = new ArrayList<>();
		try
		{
			all_error_checkers.add(new DigitalClipping(preferences));
			all_error_checkers.add(new EditClick(preferences));
			all_error_checkers.add(new InstantaneousNoise(preferences));
			all_error_checkers.add(new GroundLoopHum(preferences));
			all_error_checkers.add(new NarrowbandNoise(preferences));
			all_error_checkers.add(new Phasing(preferences));
			all_error_checkers.add(new IsNotStereo(preferences));
			all_error_checkers.add(new StereoChannelSimilarity(preferences));
			all_error_checkers.add(new StereoChannelBalance(preferences));
			all_error_checkers.add(new InsufficientDynamicRange(preferences));
			all_error_checkers.add(new InsufficientVarietyInDynamics(preferences));
			all_error_checkers.add(new InsufficientDynamicRangeCompression(preferences));
			all_error_checkers.add(new LongSilence(preferences));
			all_error_checkers.add(new DCBias(preferences));
			all_error_checkers.add(new EncodingQuality(preferences));
			all_error_checkers.add(new Duration(preferences));
		}
		catch (Exception e) // throw an exception if a constructor cannot parse a given configuration setting
		{
			throw new Exception("Invalid configuration settings specified: " + e.getMessage() + ".\n");
		}

		// Validate correctness of parsed configuration settings
		String validation_results = ConfigurationSettings.validateConfigurationSettings(all_error_checkers);
		if (validation_results != null)
			throw new Exception(validation_results);		
		
		// Return the results
		return all_error_checkers;
	}	
	
	
	/* PRIVATE METHODS **************************************************************************************/

	
	/**
	 * Parses and validates the specified command line arguments. All command line arguments must consist of
	 * -flag value pairs. An informative error message is printed to the error_stream if the command line
	 * arguments are invalid, and an explanation of the acceptable command line arguments is also printed to
	 * error_stream. A lone flag of "-help" will print the valid command line arguments to standard out.
	 * Program execution is terminated if invalid command line arguments or "-help" are parsed.
	 *
	 * @param args			The command line arguments to parse and validate.
	 * @param error_stream	The stream to write warning messages to if problems occur. May not be null.
	 * @return				The parsed and validated command line arguments. The keys indicate the flags 
	 *						present and the values indicate the values for the flags specified.
	 */
	private static HashMap<String, String> parseAndTestCommandLineArguments( String[] args,
																			 PrintStream error_stream)
	{
		// The permitted command line arguments and whether or not they are obligatory
		HashMap<String, Boolean> permitted_flags = new HashMap<>();
		permitted_flags.put("-check", false);
		permitted_flags.put("-batchcheck", false);
		permitted_flags.put("-reportcmdline", false);
		permitted_flags.put("-reporttxt", false);
		permitted_flags.put("-reporthtml", false);
		permitted_flags.put("-reportaudacity", false);
		permitted_flags.put("-reportacexml", false);
		permitted_flags.put("-reportwekaarff", false);
		permitted_flags.put("-configfile", false);

		// Explanations of what each of the permissible flags mean
		ArrayList<String> flag_explanations = new ArrayList<>();
		flag_explanations.add("The path of an audio file to test for technical production or recording errors. Either this or the -batchcheck flag must be present, but not both.");
		flag_explanations.add("The path of a directory. All audio files in this directory (but NOT its sub-directories) will be tested for technical production or recording errors. Either this or the -check flag must be present, but not both.");
		flag_explanations.add("Prints the results of jProductionCritic analysis of audio files to standard out if this flag is followed by a value of \"yes\". At least one of the report options must be selected for processing to occur.");
		flag_explanations.add("The path at which to save one or more text files containing the results of jProductionCritic analysis of audio files (one analysis file is generated for each audio file analyzed). If a single audio file is analyzed via the -check option, then this flag should specify the path of the output report file. If potentially multiple files are to be analyzed via the -batchcheck option, then this flag should specify the path of a directory in which the results files can be stored (with auto-generated file names). At least one of the report flags must be selected for processing to occur. Any files that already exist with the same paths as the generated reports will automatically be overwritten.");
		flag_explanations.add("The path at which to save one or more HTML files containing the results of jProductionCritic analysis of audio files (one analysis file is generated for each audio file analyzed). If a single audio file is analyzed via the -check option, then this flag should specify the path of the output report file. If potentially multiple files are to be analyzed via the -batchcheck option, then this flag should specify the path of a directory in which the results files can be stored (with auto-generated file names), as well as an index page. At least one of the report flags must be selected for processing to occur. Any files that already exist with the same paths as the generated reports will automatically be overwritten.");
		flag_explanations.add("The path at which to save one or more Audacity label track files containing the results of jProductionCritic analysis of audio files (one Audacity label track file is generated for each audio file analyzed). If a single audio file is analyzed via the -check option, then this flag should specify the path of the output label track file. If potentially multiple files are to be analyzed via the -batchcheck option, then this flag should specify the path of a directory in which the output files can be stored (with auto-generated file names). Any files that already exist with the same paths as the generated reports will automatically be overwritten. Note that automatically generated Audacity label track file names are given the .txt extension, which could cause them to overwrite .txt report files set to be generated in the same directory.");
		flag_explanations.add("The path at which to save a single ACE XML file containing the results of jProductionCritic analysis of one or more audio files. If a file already exists at this path, then it will automatically be overwritten. Note that, unlike the other report types, only one file is generated, regardless of whether the -check or -batchcheck extraction option is used. Also, the path specified here must always refer to a file path, not a directory, and no directory in the path will be created if it does not already exist.");
		flag_explanations.add("The path at which to save one or more Weka ARFF files containing the results of jProductionCritic analysis of audio files (one ARFF file is generated for each audio file analyzed). If a single audio file is analyzed via the -check option, then this flag should specify the path of the output ARFF file. If potentially multiple files are to be analyzed via the -batchcheck option, then this flag should specify the path of a directory in which the output files can be stored (with auto-generated file names). Any files that already exist with the same paths as the generated reports will automatically be overwritten.");
		flag_explanations.add("The path of a configuration file containing error checking settings. Will revert to the default file path if this setting is not specified. A default configuration file is auto-generated if no configuration file can be found.");
		
		// Keys for the explanations or the permissible flags
		ArrayList<String> flag_explanation_keys = new ArrayList<>();
		flag_explanation_keys.add("-check");
		flag_explanation_keys.add("-batchcheck");
		flag_explanation_keys.add("-reportcmdline");
		flag_explanation_keys.add("-reporttxt");
		flag_explanation_keys.add("-reporthtml");
		flag_explanation_keys.add("-reportaudacity");
		flag_explanation_keys.add("-reportacexml");
		flag_explanation_keys.add("-reportwekaarff");
		flag_explanation_keys.add("-configfile");
		
		// Parse the command line arguments amd perform preliminary validation on them
		HashMap<String, String> parsed_args =
			MiscellaneousMethods.parseCommandLineParameters( args,
															 permitted_flags,
															 flag_explanation_keys.toArray(new String[flag_explanation_keys.size()]),
															 flag_explanations.toArray(new String[flag_explanations.size()]),
															 error_stream );

		// To indicate if invalid command line arguments are used (beyond the basic checking of the 
		// parseCommandLineParameters method)
		boolean print_invalid_and_quit = false;

		// Perform additional validation using additional logic
		if (!parsed_args.containsKey("-check") && !parsed_args.containsKey("-batchcheck"))
		{
			error_stream.println("The command line arguments contain neither the \"-check\" nor the \"-batchcheck\" flags. At least one of these two flags must be present.");
			print_invalid_and_quit = true;
		}
		if (parsed_args.containsKey("-check") && parsed_args.containsKey("-batchcheck"))
		{
			error_stream.println("The command line arguments contain both the \"-check\" and the \"-batchcheck\" flags. Only one of these two flags may be present.");
			print_invalid_and_quit = true;
		}
		if ( !parsed_args.containsKey("-reportcmdline") && !parsed_args.containsKey("-reporttxt") &&
			 !parsed_args.containsKey("-reporthtml") && !parsed_args.containsKey("-reportaudacity") &&
			 !parsed_args.containsKey("-reportacexml") && !parsed_args.containsKey("-reportwekaarff") )
		{
			error_stream.println("The command line arguments contain none of the the \"-reportcmdline\", \"reporttxt\", \"reporthtml\", \"reportaudacity\", \"reportacexml\", or \"-reportwekaarff\" flags. At least one of these flags must be present.");
			print_invalid_and_quit = true;
		}
		
		// Terminate processing if invalid command line arguments were detected		
		if (print_invalid_and_quit)
		{
			error_stream.println("\nEXECUTION TERMINATED DUE TO INVALID COMMAND LINE ARGUMENTS");
			error_stream.flush();
			MiscellaneousMethods.parseCommandLineParameters( new String[] { "-help"},
															 permitted_flags,
															 flag_explanation_keys.toArray(new String[flag_explanation_keys.size()]),
															 flag_explanations.toArray(new String[flag_explanations.size()]),
															 error_stream );
		}

		// Return the parsed command line arguments
		return parsed_args;
	}
	
	
	/**
	 * Returns a list containing one instance of each implemented ErrorChecker subclass that is to be
	 * extracted based on the given preferences. A given ErrorChecker is chosen only if its should_extract_tag
	 * is present in the given preferences and is set to "true" (case does not matter). Quits execution if
	 * no ErrorChecker objects are found. This method also verifies that the settings stored in the
	 * preferences argument are valid.
	 * 
	 * @param preferences	The execution settings indicating, among other things, whether or not each
	 *						ErrorChecker algorithm is to be applied. May not be null.
	 * @return				One instance of each ErrorChecker algorithm chosen to be applied during error
	 *						checking.
	 * @throws Exception	An informative exception is thrown if preferences do not specify any error
	 *						checkers to be applied, or if preferences include invalid settings for any of
	 *						the ErrorCheckers.
	 */
	private static ArrayList<ErrorChecker> getChosenErrorCheckers( ConfigurationSettings preferences )
		throws Exception
	{
		// A set of all implemented ErrorChecker algorithms (also checks validity of preferences)
		ArrayList<ErrorChecker> all_error_checkers = getAllErrorCheckers(preferences);

		// Fill chosen_error_checkers with all ErrorChecker algorithms set to be applied
		ArrayList<ErrorChecker> chosen_error_checkers = new ArrayList<>();
		for (int i = 0; i < all_error_checkers.size(); i++)
		{
			String this_should_extract_tag = all_error_checkers.get(i).getShouldExtractTag();
			String this_preference_setting = preferences.getConfigurationSetting(this_should_extract_tag);
			if (this_preference_setting != null)
			{
				if (this_preference_setting.toLowerCase().equals("true"))
					chosen_error_checkers.add(all_error_checkers.get(i));
			}
		}
		
		// Terminate processing if no error checkers are marked for application
		if (chosen_error_checkers.isEmpty())
			throw new Exception("No error checkers set to be applied in the configuration settings.");
		
		// Return the results		
		return chosen_error_checkers;
	}
	
	
	/**
	 * Return a list of files to process based on the contents of execution_settings. Some basic error
	 * checking is performed to make sure that all returned files exist and are readable. Quits execution if
	 * no valid files are found.
	 * 
	 * @param execution_settings	The settings to base processing on. If the -check key is present, then
	 *								its value is interpreted as the single file name to take. If the
	 *								-batchcheck key is instead present, its value is interpreted as the path
	 *								of a directory, and all files in this directory (but NOT its 
	 *								sub-directories) will be taken.
	 * @param error_stream			The stream to write warning messages to if problems occur, such as files
	 *								that are not readable. May not be null.
	 * @return						The files to process. May or may not be audio files (this is not validated
	 *								in this method. Will not be null, but may be of size zero.
	 * @throws Exception			An informative exception is thrown if execution_settings refers to a file
	 *								or directory that does not exist (or, in the latter case, is empty of 
	 *								files).
	 */
	private static ArrayList<File> getFilesToCheck( HashMap<String, String> execution_settings,
													PrintStream error_stream )
		throws Exception
	{
		// The files found
		ArrayList<File> files_to_process = new ArrayList<>();
		
		// Acquire a single file
		if (execution_settings.containsKey("-check"))
		{
			try
			{
				File this_file = new File(execution_settings.get("-check"));
				if (FileMethods.validateFile(this_file, true, false))
					files_to_process.add(this_file);
			}
			catch (Exception e) {error_stream.println(e.getMessage());}
		}
		
		// Acquire potentially many files from a directory
		else if (execution_settings.containsKey("-batchcheck"))
		{
			File directory = new File(execution_settings.get("-batchcheck"));
			ArrayList<File> preliminary_files = new ArrayList<>();
			try
			{
				FileMethods.addAllFilesInDirectory( directory,
													false,
													null,
													preliminary_files );
				for (int i = 0; i < preliminary_files.size(); i++)
				{
					try
					{
						File this_file = preliminary_files.get(i);
						if (FileMethods.validateFile(this_file, true, false))
							files_to_process.add(preliminary_files.get(i));
					}
					catch (Exception e)
					{
						error_stream.println(e.getMessage());
						//e.printStackTrace();
					}
				}
			}
			catch (Exception e)
			{
				error_stream.println(e.getMessage());
				//e.printStackTrace();
			}
		}
		
		// Terminate processing if no files were found
		if (files_to_process.isEmpty())
			throw new Exception("No files found at the specified path.");
		
		// Return the results
		return files_to_process;
	}
	
	
	/**
	 * Opens the given audio file, creates a list of MetaData objects containing its metadata, looks for
	 * errors in the file, creates a list of ErrorReports noting any errors found and adds the two lists 
	 * respectively to combined_metadata and combined_error_reports, before finally returning the total number
	 * of errors found.
	 * 
	 * @param audio_file				The audio file to process.
	 * @param combined_metadata			Metadata for all files that have been and will be processed. The outer
	 *									index indicates the file and the inner index indicates the piece of
	 *									metadata. The outer index must match that of combined_error_reports.
	 *									May not be null and may not contain nulls.
	 * @param combined_error_reports	Error reports for all files that have been and will be processed. The
	 *									outer index indicates the file and the inner index indicates the type
	 *									of error. The outer index must match that of combined_metadata.
	 *									May not be null and may not contain nulls.
	 * @param error_checkers_to_apply	One instance of each ErrorChecker algorithm chosen to be applied 
	 *									during error checking.
	 * @param preferences				The execution settings based on the configurations file and/or default
	 *									settings.
	 * @param status_stream				A stream to print updates to regarding each error checked for. May be
	 *									null, in which case no status updates are output.
	 * @param error_stream				The stream to write warning messages to if problems occur during 
	 *									processing of particular error checkers. Not used if null, but if it
	 *									is null, then an exception is thrown if a problem occurs with a
	 *									particular error checker.
	 * @return							The number of errors found in audio_file.
	 * @throws Exception				An informative Exception is thrown if audio_file does not refer to a
	 *									compatible audio file or if there is an error during processing.
	 */
	private static int processFile( File audio_file,
									ArrayList<ArrayList<MetaData>> combined_metadata, 
									ArrayList<ArrayList<ErrorReport>> combined_error_reports,
									ArrayList<ErrorChecker> error_checkers_to_apply,
									ConfigurationSettings preferences,
									PrintStream status_stream,
									PrintStream error_stream )
		throws Exception
	{
		// Parse the audio file, without processing or converting it in any way. Throw Exception if necessary.
		AudioSamples audio_data = new AudioSamples( audio_file, "", false );
		
		// Look for errors and add them to total_errors_found
		ArrayList<ErrorReport> total_errors_found = new ArrayList<>();
		for (int i = 0; i < error_checkers_to_apply.size(); i++)
		{
			try
			{	
				// Generate error reports
				if (status_stream != null)
					status_stream.print("\t\t>>> Checking for " + error_checkers_to_apply.get(i).getTitle() + "  . . . ");
				ArrayList<ErrorReport> errors = error_checkers_to_apply.get(i).checkForErrors(audio_data);

				// Merge errors that are too close together
				int proximity_threshold = Integer.valueOf(preferences.getConfigurationSetting("error_report_merge_proximity_maximum"));
				errors = ErrorReport.mergeErrorReports(errors, proximity_threshold);

				// Add the error reports to the overall list
				for (int error = 0; error < errors.size(); error++)
					total_errors_found.add(errors.get(error));
				if (status_stream != null)
					status_stream.print(errors.size() + " found\n");
			}
			catch (Exception e)
			{
				if (error_stream != null)
				{
					error_stream.println("Error checking " + audio_file.getName() + " for " + error_checkers_to_apply.get(i).getTitle() + ": " + e.getMessage());
					// e.printStackTrace();
				}
				else throw new Exception("Error checking for " + error_checkers_to_apply.get(i).getTitle() + ": " + e.getMessage());
			}
		}
		
		// Acquire metadata
		AudioFileFormat audio_file_format = AudioSystem.getAudioFileFormat(audio_file);
		AudioFormat audio_format = audio_data.getAudioFormat();
		ArrayList<MetaData> metadata_found = new ArrayList<>();
		metadata_found.add(new MetaData("FILE NAME", audio_file.getName()));
		metadata_found.add(new MetaData("FILE PATH", audio_file.getCanonicalPath()));
		metadata_found.add(new MetaData("FILE TYPE", audio_file_format.getType().toString()));
		metadata_found.add(new MetaData("DURATION", StringMethods.getFormattedDuration( (long) (1000.0 * audio_data.getDuration()) )));
		metadata_found.add(new MetaData("FILE SIZE", (audio_file.length() / 1024) + " KB"));
		metadata_found.add(new MetaData("CHANNELS", new Integer(audio_format.getChannels()).toString()));
		metadata_found.add(new MetaData("SAMPLING RATE", (audio_format.getSampleRate() / 1000) + " kHz"));
		metadata_found.add(new MetaData("BIT DEPTH", audio_format.getSampleSizeInBits() + " bits"));
		metadata_found.add(new MetaData("ENCODING", audio_format.getEncoding().toString()));
        String endian; 
		if (audio_format.isBigEndian()) endian = "Big-Endian";
        else endian = "Little-Endian";
		metadata_found.add(new MetaData("BYTE ORDER", endian));
		// metadata_found.add(new MetaData("FRAMES OF AUDIO DATA", audio_file_format.getFrameLength() + " sample frames"));
		// metadata_found.add(new MetaData("FRAME SIZE", (8 * audio_format.getFrameSize()) + " bits"));
		// metadata_found.add(new MetaData("FRAME RATE", audio_format.getFrameRate() + " frames per second"));
		// metadata_found.add(new MetaData("FILE PROPERTIES", audio_file_format.properties().toString()));
		// metadata_found.add(new MetaData("AUDIO PROPERTIES", audio_format.properties().toString()));
		
		// Store errors found and metadata
		combined_error_reports.add(total_errors_found);
		combined_metadata.add(metadata_found);
		
		// Return the total number of errors found
		return total_errors_found.size();			
	}
	
	
	/**
	 * Output/save metadata and error reports for all files processed.
	 * 
	 * @param combined_metadata			Metadata for all files that have been processed. The outer index 
	 *									indicates the file and the inner index indicates the piece of
	 *									metadata. The outer index must match that of combined_error_reports.
	 *									May not be null and may not contain nulls.
	 * @param combined_error_reports	Error reports for all files that have been processed. The outer index 
	 *									indicates the file and the inner index indicates the type of error.
	 *									The outer index must match that of combined_metadata. May not be null
	 *									and may not contain nulls.
	 * @param error_checkers_applied	One instance of each ErrorChecker algorithm chosen that was applied 
	 *									during error checking. May not be null.
	 * @param execution_settings		Specifies which types of reports are to be generated and, if 
	 *									appropriate, where they should be saved.
	 * @param status_stream				The stream to write command line reports to (if this option is
	 *								    selected in execution_settings).
	 * @param error_stream				The stream to write warning messages to if problems occur, such as
	 *									files that cannot be saved. May not be null.
	 */
	private static void outputReports( ArrayList<ArrayList<MetaData>> combined_metadata,
									   ArrayList<ArrayList<ErrorReport>> combined_error_reports,
									   ArrayList<ErrorChecker> error_checkers_applied,
								       HashMap<String, String> execution_settings,
									   PrintStream status_stream,
									   PrintStream error_stream )
	{
		// Print reports to sandard out
		if (execution_settings.containsKey("-reportcmdline"))
		{
			if (execution_settings.get("-reportcmdline").toLowerCase().equals("yes"))
			{
				for (int i = 0; i < combined_metadata.size(); i++)
				{	
					String report = ReportBuilder.generateTextReport(combined_metadata.get(i), combined_error_reports.get(i));
					status_stream.println("-------- START OF REPORT " + (i+1) + " --------\n");
					status_stream.print(report);
					status_stream.println("-------- END OF REPORT " + (i+1) + " ----------\n");
				}
			}
		}

		// Save text file(s)
		if (execution_settings.containsKey("-reporttxt")) 
		{
			// Prepare the reports
			String[] reports = new String[combined_metadata.size()];
			for (int i = 0; i < combined_metadata.size(); i++)
				reports[i] = ReportBuilder.generateTextReport(combined_metadata.get(i), combined_error_reports.get(i));
			
			// In the case of a single file to generate
			if (execution_settings.containsKey("-check"))
			{
				String save_path = execution_settings.get("-reporttxt");
				File save_file = new File(save_path);
				try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(save_file)))
					{ writer.writeBytes(reports[0]); }
				catch (Exception e)
					{ error_stream.println("Error when trying to save text report to \"" + save_path + "\": " + e.getMessage()); }
			}

			// In the case of multiple files to generate
			else if (execution_settings.containsKey("-batchcheck"))
			{
				String directory_path = execution_settings.get("-reporttxt");
				saveReport(reports, ".txt", directory_path, combined_metadata, error_stream);
			}
		}

		// Save HTML file(s)
		if (execution_settings.containsKey("-reporthtml")) 
		{
			// Prepare the reports
			String[] reports = new String[combined_metadata.size()];
			for (int i = 0; i < combined_metadata.size(); i++)
				reports[i] = ReportBuilder.generateHtmlReport(combined_metadata.get(i), combined_error_reports.get(i));
			
			// In the case of a single file to generate
			if (execution_settings.containsKey("-check"))
			{
				String save_path = execution_settings.get("-reporthtml");
				File save_file = new File(save_path);
				try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(save_file)))
					{ writer.writeBytes(reports[0]); }
				catch (Exception e)
					{ error_stream.println("Error when trying to save HTML report to \"" + save_path + "\": " + e.getMessage()); }
			}

			// In the case of multiple files to generate
			else if (execution_settings.containsKey("-batchcheck"))
			{
				String directory_path = execution_settings.get("-reporthtml");
				saveReport(reports, ".html", directory_path, combined_metadata, error_stream);
			}
		}

		// Save Audacity Label file(s)
		if (execution_settings.containsKey("-reportaudacity")) 
		{
			// Prepare the reports
			String[] reports = new String[combined_metadata.size()];
			for (int i = 0; i < combined_metadata.size(); i++)
				reports[i] = ReportBuilder.generateAudacityLabelTrack(combined_error_reports.get(i));
			
			// In the case of a single file to generate
			if (execution_settings.containsKey("-check") && reports[0] != null)
			{
				String save_path = execution_settings.get("-reportaudacity");
				File save_file = new File(save_path);
				try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(save_file)))
					{ writer.writeBytes(reports[0]); }
				catch (Exception e)
					{ error_stream.println("Error when trying to save Audacity label file to \"" + save_path + "\": " + e.getMessage()); }
			}

			// In the case of multiple files to generate
			else if (execution_settings.containsKey("-batchcheck"))
			{
				String directory_path = execution_settings.get("-reportaudacity");
				saveReport(reports, ".txt", directory_path, combined_metadata, error_stream);
			}
		}

		// Save ACE XML file
		if (execution_settings.containsKey("-reportacexml")) 
		{
			// Prepare the reports
			SegmentedClassification[] reports = new SegmentedClassification[combined_metadata.size()];
			for (int i = 0; i < combined_metadata.size(); i++)
				reports[i] = ReportBuilder.generateAceXmlClassification(combined_metadata.get(i), combined_error_reports.get(i));
			
			// Save the report
			String save_path = execution_settings.get("-reportacexml");
			File save_file = new File(save_path);
			try { SegmentedClassification.saveClassifications( reports, save_file, "Generated by jProductionCritic" ); }
			catch (Exception e)
				{ error_stream.println("Error when trying to save the ACE XML report to \"" + save_path + "\": " + e.getMessage()); }
		}

		// Save Weka ARFF file(s)
		if (execution_settings.containsKey("-reportwekaarff")) 
		{
			// Prepare the reports
			String[] reports = new String[combined_metadata.size()];
			for (int i = 0; i < combined_metadata.size(); i++)
				reports[i] = ReportBuilder.generateWekaArffFile(combined_error_reports.get(i), error_checkers_applied);
			
			// In the case of a single file to generate
			if (execution_settings.containsKey("-check"))
			{
				String save_path = execution_settings.get("-reportwekaarff");
				File save_file = new File(save_path);
				try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(save_file)))
					{ writer.writeBytes(reports[0]); }
				catch (Exception e)
					{ error_stream.println("Error when trying to save Weka ARFF file to \"" + save_path + "\": " + e.getMessage()); }
			}

			// In the case of multiple files to generate
			else if (execution_settings.containsKey("-batchcheck"))
			{
				String directory_path = execution_settings.get("-reportwekaarff");
				saveReport(reports, ".arff", directory_path, combined_metadata, error_stream);
			}
		}
	}
	
	
	/**
	 * Save all of the specified reports to the specified directory using auto-generated file names. Each
	 * auto-generated file name will consist of the file name of the audio file from which the report was 
	 * generated (with the extension stripped away), followed by an underscore, followed by the number of the
	 * report (starting at 1), followed by the extension specified in the file_extension parameter. A new
	 * directory is created if one does not already exist at the specified save_directory_path (note that
	 * nested directories will not be created, however, and trying to do so will generate an error). Any files
	 * in the save directory with filenames the same as those saved by this method will automatically be 
	 * overwritten.
	 *  
	 * @param reports					The contents of the files to save. The index indicates the file. This
	 *									index must match that of combined_metadata. May not be null.
	 * @param file_extension			The extension to add to the files saved. Should include the period.
	 * @param save_directory_path		The path of the directory to save the generated files to. May or may
	 *									not exist yet.
	 * @param combined_metadata			Metadata for all files that have been processed. The outer index 
	 *									indicates the file and the inner index indicates the piece of
	 *									metadata. The outer index must match that of reports. May not be null
	 *									and may not contain nulls.
	 * @param error_stream				The stream to write warning messages to if problems occur, such as
	 *									files that cannot be saved or being unable to create a directory. May
	 *									not be null.
	 */
	private static void saveReport( String[] reports,
									String file_extension,
			                        String save_directory_path,
									ArrayList<ArrayList<MetaData>> combined_metadata,
									PrintStream error_stream )
	{
		// Create a save directory if necessary
		boolean directory_created = FileMethods.createDirectory(save_directory_path);
		if (!directory_created)
			error_stream.println("Error when trying to create a directory to hold text reports at \"" + save_directory_path + "\".");
		
		// Save the files
		else
		{
			for (int i = 0; i < combined_metadata.size(); i++)
			{
				if (reports[i] != null)
				{
					// Construct the save file name
					String file_name = "";
					ArrayList<MetaData> this_metadata = combined_metadata.get(i);
					for (int j = 0; j < this_metadata.size(); j++)
						if (this_metadata.get(j).identifier.equals("FILE NAME"))
							file_name = this_metadata.get(j).value;
					String temp_file_name = StringMethods.removeExtension(file_name);
					if (temp_file_name != null)
						file_name = temp_file_name;
					file_name += "_" + (i+1);
					file_name += file_extension;

					// Construct the save path
					String save_path = save_directory_path + File.separator + file_name;

					// Save the report
					File save_file = new File(save_path);
					try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(save_file)))
						{ writer.writeBytes(reports[i]); }
					catch (Exception e)
						{ error_stream.println("Error when trying to save report to \"" + save_path + "\": " + e.getMessage()); }
				}
			}
		}
	}
}