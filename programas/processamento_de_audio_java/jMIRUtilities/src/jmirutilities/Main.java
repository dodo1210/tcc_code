/*
 * Main.java
 * Version 1.4.5
 * CIRMMT, Marianopolis College and McGill University
 * 
 * Last modified on May 5, 2017.
 */
package jmirutilities;

import java.io.*;
import java.util.Vector;
import ace.datatypes.*;
import jMusicMetaManager.RecordingMetaData;
import jsongminer.EchoNestMiner;
import mckay.utilities.general.FileFilterImplementation;
import mckay.utilities.staticlibraries.FileMethods;
import com.echonest.api.v4.EchoNestAPI;

/**
 * This utility performs various utility tasks for the jMIR package, generally
 * related to generating labelled ACE XML Classification Files, file parsing,
 * file type conversions and combining existing ARFF or ACE XML files in various
 * ways. It can only be run from the command line. The first command line
 * argument must be a flag with a hyphen preceding it, and must be one of the
 * valid choices specified in the help. Such a flag specifies the task to be
 * performed. Some flags permit additional command line options afterwards,
 * which do not require hyphens. Flags, associated functionality and options can
 * be displayed by running this with  the "-help" flag.
 *
 * @author Cory McKay
 */
public class Main
{
	/**
	 * Runs the software from the command line. Run with the arguments "help"
	 * or "-help" for more information
	 *
	 * @param args  The command line arguments.
	 */
	public static void main(String[] args)
	{
		// Output an error if not flags are provided
		if (args.length == 0)
		{
			listOptions(true);
			System.exit(-1);
		}

		// Convert an ACE XML Feature Values file to CSV
		if (args[0].equals("-convertFeatureValuesToCSV"))
		{
			if (args.length != 3)
			{
				System.err.println("ERROR: The -convertFeatureValuesToCSV option must be followed by two parameters, the first specifying path of the ACE XML Feature Values file to convert and the second specifying an ACE XML Feature Definitions file associted with it.");
				System.exit(-1);
			}
			convertACEXMLtoCSV(args[1], args[2]);
		}

		// Convert an ACE XML Feature Values file to Weka ARFF
		if (args[0].equals("-convertFeatureValuesToARFF"))
		{
			if (args.length != 3)
			{
				System.err.println("ERROR: The -convertFeatureValuesToARFF option must be followed by two parameters, the first specifying path of the ACE XML Feature Values file to convert, and the second specifying the path of the new Weka ARFF file to save to.");
				System.exit(-1);
			}
			convertACEXMLtoARFF(args[1], args[2]);
		}

		// Perform fingerprinting
		else if (args[0].equals("-batchAudioIdentify"))
		{
			if (args.length != 4)
			{
				System.err.println("ERROR: The -batchAudioIdentify option must be followed by three parameters, the first specifying the audio root directory, the second specifying the output file path and the thrid specifying an Echo Nest API Key (to enable access to Echo Nest fingerprinting web services).");
				System.exit(-1);
			}
			batchAudioIdentify(args[1], args[2], args[3]);
		}

		// Bring up a GUI for labelling instances based on file paths
		else if (args[0].equals("-fileLabelInstances"))
		{
			InstanceLabeller labeller = new InstanceLabeller();
		}

		// Label instances based on a tab delimited text file
		else if (args[0].equals("-txtLabelInstances"))
		{
			// Extract information from the command line
			String input_file_path = null;
			if (args.length > 1)
			{
				input_file_path = args[1];
			}
			String output_file_path = null;
			if (args.length > 2)
			{
				output_file_path = args[2];
			}
			String[] field_types = null;

			// Extract data and save it
			tabDelimitedToACEXML(input_file_path, output_file_path);
		}

		// Merge extracted features
		else if (args[0].equals("-mergeFeatures"))
		{
			// Extract information from the command line
			String input_feature_vector_file_path_1 = null;
			if (args.length > 1)
			{
				input_feature_vector_file_path_1 = args[1];
			}
			String input_feature_definitions_file_path_1 = null;
			if (args.length > 2)
			{
				input_feature_definitions_file_path_1 = args[2];
			}
			String input_feature_vector_file_path_2 = null;
			if (args.length > 3)
			{
				input_feature_vector_file_path_2 = args[3];
			}
			String input_feature_definitions_file_path_2 = null;
			if (args.length > 4)
			{
				input_feature_definitions_file_path_2 = args[4];
			}
			String matching_key_file_path = null;
			if (args.length > 5)
			{
				matching_key_file_path = args[5];
			}
			String output_feature_vector_file_path = null;
			if (args.length > 6)
			{
				output_feature_vector_file_path = args[6];
			}
			String output_feature_definitions_file_path = null;
			if (args.length > 7)
			{
				output_feature_definitions_file_path = args[7];
			}

			// Extract data and save it
			mergeFeatures(input_feature_vector_file_path_1,
					input_feature_definitions_file_path_1,
					input_feature_vector_file_path_2,
					input_feature_definitions_file_path_2,
					matching_key_file_path,
					output_feature_vector_file_path,
					output_feature_definitions_file_path);
		}

		// Merge different instances stored in diffrent files
		else if (args[0].equals("-mergeInstances"))
		{
			// Extract information from the command line
			String directory_to_search = null;
			if (args.length > 1)
				directory_to_search = args[1];
			String output_file_path = null;
			if (args.length > 2)
				output_file_path = args[2];

			// Parse, merge and save
			mergeInstances(directory_to_search, output_file_path);
		}

		// Extract fields from an iTunes file and output them to a text file
		else if (args[0].equals("-matchItunesFields"))
		{
			// Extract information from the command line
			String input_file_path = null;
			if (args.length > 1)
				input_file_path = args[1];
			String output_file_path = null;
			if (args.length > 2)
				output_file_path = args[2];
			String[] field_types = null;
			if (args.length > 3)
			{
				field_types = new String[args.length - 3];
				for (int i = 3; i < args.length; i++)
					field_types[i - 3] = args[i];
			}

			// Extract data and save it
			matchItunesFields(input_file_path, output_file_path, field_types);
		}

		// Generate primary keys
		else if (args[0].equals("-generatePrimaryKeys"))
			generatePrimaryKeys(args[1], args[2], args[3]);

		// Modify the identifiers of instances in an ACE XML Feature Values file
		else if (args[0].equals("-modifyInstanceIdentifiers"))
			modifyInstanceIdentifiers(args[1], args[2], args[3], args[4]);

		// Split a features values file into multiple files, with one feature each
		else if (args[0].equals("-splitInstancesByFeatureType"))
			splitInstancesByFeatureType(args[1], args[2]);

		// List the available command line arguments
		else if (args[0].equals("-help") || args[0].equals("help"))
			listOptions(false);
						
		// Indicate to the user that valid command line options were not provided
		else listOptions(true);
	}


	/* PRIVATE METHODS *******************************************************/

	
	/**
	 * Parse the specified <i>ace_xml_feature_values_file_path</i> ACE XML Feature 
	 * Value file and its associated <i>ace_xml_feature_definitions_file_path</i> 
	 * ACE XML Feature Definitions file, and convert the feature values into
	 * a new CSV file with the same name as the Feature Values file, but with a CSV extension.
	 * Note that any existing file at the output path is overwritten. This CSV file
	 * lists instance identifiers in the first column, and each additional column
	 * corresponds to a different feature
	 *
	 * @param ace_xml_feature_values_file_path		The path of the input ACE XML file
	 *												containing feature values that is
	 *												to be parsed and converted.
	 * @param ace_xml_feature_definitions_file_path	The path of the input ACE XML file
	 *												holding feature definitions corresponding
	 *												to the feature values.
	 */
	private static void convertACEXMLtoCSV( String ace_xml_feature_values_file_path,
			String ace_xml_feature_definitions_file_path )
	{
		try
		{
			jsymbolic2.processing.AceXmlConverter.saveAsArffOrCsvFiles( ace_xml_feature_values_file_path,
					ace_xml_feature_definitions_file_path,
					false,
					true,
					System.err );
		}
		catch (Exception e)
		{
			System.err.println("ERROR: Could not succesfully translate the specified ACE XML Feature Values file to a CSV file.");
			System.exit(-1);
		}
	}
	
	
	/**
	 * Parse the specified <i>ace_xml_input_file_path</i> ACE XML Feature Value
	 * file and convert it into a new Weka ARFF file at the path specified by
	 * <i>weka_arff_output_file_path</i>. Note that any existing file at the
	 * output path is overwritten.
	 *
	 * <p>The instance identifiers stored in the ACE XML file are printed to
	 * standard out, as these cannot be stored in a meaningful way in Weka ARFF
	 * files. Also, the Weka relation name is defaulted to
	 * "Converted_from_ACE_XML".
	 *
	 * @param ace_xml_input_file_path		The path of the input ACE XML file
	 *										containing feature values that is
	 *										to be parsed and converted.
	 * @param weka_arff_output_file_path	The path of the new Weka ARFF file
	 *										to save to.
	 */
	private static void convertACEXMLtoARFF( String ace_xml_input_file_path,
			String weka_arff_output_file_path )
	{
		// Stores parsed contents of the ACE XML file
		ace.datatypes.DataBoard feature_values = null;

		// Parse the ACE XML file
		try
		{
			String[] input_files = new String[1];
			input_files[0] = ace_xml_input_file_path;
			feature_values = new ace.datatypes.DataBoard(null, null, input_files, null);
		}
		catch (Exception e)
		{
			System.err.println("ERROR: Could not succesfully parse the file specified at that path: " + ace_xml_input_file_path + ". Perhaps this file does not exist, or is not a valid ACE XML Feature Values file?");
			System.exit(-1);
		}

		// Convert and save to Weka ARFF and output the instance identifiers to standard out
		try
		{
			File output_file = new File(weka_arff_output_file_path);
			String relation_name = "Converted_from_ACE_XML";
			String[] instance_identififiers = feature_values.saveToARFF(relation_name, output_file, true, true);

			if (instance_identififiers != null)
				for (int i = 0; i < instance_identififiers.length; i++)
					System.out.println(instance_identififiers[i]);
		}
		catch (Exception e)
		{
			System.err.println("ERROR: Could not succesfully save the Weka ARFF file to the path:" + weka_arff_output_file_path + ". Perhaps you do not have write permission to this path?");
			System.exit(-1);
		}
	}


	/**
	 * Search through the specified <i>audio_root_directory</i> to find all
	 * files in this directory or its sub-directories with an MP3 extension.
	 * Each of these files is then identified using jSongMiner's fingerprinting
	 * functionality. A tab-delimitted text file is then saved to the
	 * specified <i>output_file_path</i>. This output file lists, on each line,
	 * the file path of one of the found audio files, its title and the
	 * associated artist. If the song title and/or artist cannot be found for
	 * a given file, the corresponding entry is marked as 
	 * "COULD_NOT_BE_IDENTIFIED". Note that progress is printed to standard
	 * out as it progresses.
	 *
	 * <b>IMPORTANT:</b> Since the goald of this method is to be of use to
	 * evaluate fingerprinting, only fingerprinting is used, and any embedded
	 * metadata is ignored.
	 *
	 * @param audio_root_directory	The directory to recursively search for
	 *								audio files.
	 * @param output_file_path		The path to save identifying information for
	 *								each of the found audio files to.
	 * @param en_api_key			The Echo Nest API Key to use for accessing
	 *								Echo Nest web services.
	 */
	private static void batchAudioIdentify( String audio_root_directory,
			String output_file_path,
			String en_api_key )
	{
		// Specify the recognized file extensions. Only MP3s are specified here,
		// but the underlying jSongMiner and EchoNest fingerprinting
		// technologies can handle other file formats as well
		String[] recognized_extensions = {"mp3"};
		FileFilterImplementation filter = new FileFilterImplementation(recognized_extensions);

		// Prepare a list of files of the specified type in the specified
		// directory and its sub-directories. End execution if none are found.
		File[] audio_files = FileMethods.getAllFilesInDirectory( new File (audio_root_directory),
				true,
				filter,
				null );
		if (audio_files == null)
		{
			System.err.println("ERROR: Could not find any compatible audio files in the " + audio_root_directory + " directory or its sub-directories.");
			System.exit(-1);
		}

		// Prepare and test access to the Echo Nest API
		EchoNestMiner en_miner = null;
		try
		{
			// Test access to Echo Nest API through dummy request
			EchoNestAPI echonest_api = new com.echonest.api.v4.EchoNestAPI(en_api_key);
			echonest_api.setTraceRecvs(false);
			echonest_api.setTraceSends(false);
			echonest_api.topHotArtists(1);

			// Initialize jSongMiner with Echo Nest API
			en_miner = new EchoNestMiner(en_api_key);
		}
		catch (Exception e)
		{
			System.err.println("ERROR: Could not succesfully initialize Echo Nest fingerprinting services. Perhaps an invalid Echo Nest API key was provided, or Internet access is not available?");
			System.exit(-1);
		}

		// Prepare the output writer
		FileWriter output_writer = null;
		try	{ output_writer = new FileWriter(new File(output_file_path)); }
		catch (IOException e)
		{
			System.err.println("ERROR: The specified output path " + output_file_path + " could not be written to.");
			System.exit(-1);
		}

		// Identify audio files and save identifying data
		try
		{
			System.out.println("Found " + audio_files.length + " audio files. Now identifying...");
			for (int i = 0; i < audio_files.length; i++)
			{
				// Store default result values
				String path = audio_files[i].getAbsolutePath();
				String title = "COULD_NOT_BE_IDENTIFIED";
				String artist = "COULD_NOT_BE_IDENTIFIED";

				// Note progress
				System.out.print("\nProcessing " + (i+1) + " of " + audio_files.length + ": " + path + "... ");

				// Perform remote fingerprinting
				String song_id = en_miner.getAndStoreEchoNestSongIDFromRemoteFingerPrint( audio_files[i].getAbsolutePath(), false );

				// Get identifying title and artist if fingerprinting succeeded
				if (song_id != null)
				{
					String[] song_identifiers = en_miner.getIdentifyingMetadataForSong(song_id);
					if (song_identifiers != null)
					{
						if (song_identifiers[0] != null)
							title = song_identifiers[0];
						if (song_identifiers[1] != null)
							artist = song_identifiers[1];
						System.out.println(title + " BY " + artist);
					}
				}
				else System.out.println(" FAILED TO ACQUIRE AN ID FOR " + audio_files[i].getAbsolutePath());

				// Save the results
				output_writer.write(path + "\t" + title + "\t" + artist);
				if (i != audio_files.length - 1)
					output_writer.write("\n");
			}

			// Close the output flie
			output_writer.close();

			// Note completion
			System.out.println("\n" + audio_files.length + " audio files found and identified.");
		}
		catch (IOException e)
		{
			System.err.println("ERROR: Could not complete writing to " + output_file_path + ".");
			System.exit(-1);
		}
	}


	/**
	 * Generate a model classifications ACE XML Classifications File based on
	 * the specified tab delimited text input file. Each instance corresponds
	 * to a line in the text file. The first entry on the line is the name of
	 * the instance and the second (or more) are the name(s) of its class(es).
	 *
	 * <p>Multiple labels may be assigned to an instance, but this method
	 * does not allow hierarchical labelling (although there is nothing in the
	 * underlying ACE classes that prevents it).
	 *
	 * <p>Errors are output to the command line.
	 *
	 * @param   input_file_path   The path of the text file to parse.
	 * @param   output_file_path  The path of the ACE XML file to generate.
	 */
	private static void tabDelimitedToACEXML(String input_file_path,
			String output_file_path)
	{
		try
		{
			// Do error checking and open the input file
			File input_file = null;
			if (input_file_path == null)
			{
				throw new Exception("No input file specified.");
			}
			if (output_file_path == null)
			{
				throw new Exception("No output file specified.");
			}

			// Open the input and output files file
			try
			{
				input_file = new File(input_file_path);
			} catch (Exception e)
			{
				throw new Exception("Unable to open input file at the specified path: " + input_file_path);
			}

			// Parse the input text file
			String[] lines_of_file = mckay.utilities.staticlibraries.FileMethods.parseTextFileLines(input_file);
			String[][] parsed_data = new String[lines_of_file.length][];
			for (int line = 0; line < lines_of_file.length; line++)
			{
				try
				{
					parsed_data[line] = mckay.utilities.staticlibraries.StringMethods.breakIntoTokens(lines_of_file[line], "\t");
				} catch (Exception e)
				{
					throw new Exception("Unable to parse the specified file: " + input_file_path + "\nProblem occured at line " + (line + 1) + ": " + lines_of_file[line]);
				}
			}

			// Prepare the array of model classifications
			SegmentedClassification[] combined_classifications = new SegmentedClassification[parsed_data.length];

			// Construct model classifications
			try
			{
				// Add information instance by instance
				for (int instance = 0; instance < combined_classifications.length; instance++)
				{
					// Prepare the instance
					combined_classifications[instance] = new SegmentedClassification();

					// Add the instance label
					combined_classifications[instance].identifier = parsed_data[instance][0];

					// Add the class name(s)
					if (parsed_data[instance].length > 1)
					{
						String[] classes = new String[parsed_data[instance].length - 1];
						for (int class_index = 0; class_index < classes.length; class_index++)
						{
							classes[class_index] = parsed_data[instance][class_index + 1];
						}
						combined_classifications[instance].classifications = classes;
					}
				}
			} catch (Exception e)
			{
				throw new Exception("Unable to generate model classification from the input file: " + input_file_path);
			}

			// Save the model classifications
			try
			{
				File output_file = new File(output_file_path);
				SegmentedClassification.saveClassifications(combined_classifications, output_file, "");
			} catch (Exception e)
			{
				throw new Exception("Unable to write to the specified output path: " + output_file_path);
			}
		} // Output any errors to the command line
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
		// e.printStackTrace();
		}
	}


	/**
	 * Merges the extracted feature contained in two different ACE XML Feature
	 * Vector files and their corresponding Feature Definition Files. The two
	 * input Feature Vector files should refer to different features extracted
	 * for the same instances. This could be useful, for example, for combining
	 * features extracted from jAudio with features extracted with jWebMiner
	 * for the same songs.
	 *
	 * <p>NOTE THAT THIS HAS NOT BEEN DESIGNED TO WORK WITH DATASETS THAT
	 * HAVE SUBSETS YET (e.g. features separately extracted separately for
	 * windows). Such subsets are currently just ignored.
	 *
	 * @param input_feature_vector_file_path_1   The first input feature vector
	 *                                           file path.
	 * @param input_feature_defs_file_path_1     The feature definitions file
	 *                                           for this first feature vector
	 *                                           file.
	 * @param input_feature_vector_file_path_2   The second input feature
	 *                                           vector file
	 * @param input_feature_defs_file_path_2     The feature definitions file
	 *                                           for this second feature vector
	 *                                           file.
	 * @param matching_key_file_path             A tab delimited text file,
	 *                                           such as one that might be
	 *                                           output by the
	 *                                           matchItunesFields method,
	 *                                           where each line corresponds to
	 *                                           an instance and each line has
	 *                                           three pieces of information
	 *                                           separated by tabs, namely the
	 *                                           instance identifier for the
	 *                                           fist feature vector file, the
	 *                                           corresponding instance
	 *                                           identifier for the second
	 *                                           feature vector file, and the
	 *                                           instance identifier to use for
	 *                                           the output file.
	 * @param output_feature_vector_file_path    The file path to save the
	 *                                           combined feature vector file
	 *                                           to.
	 * @param output_feature_defs_file_path      The file path to save the
	 *                                           combined feature definitions
	 *                                           file to
	 */
	private static void mergeFeatures(String input_feature_vector_file_path_1,
			String input_feature_defs_file_path_1,
			String input_feature_vector_file_path_2,
			String input_feature_defs_file_path_2,
			String matching_key_file_path,
			String output_feature_vector_file_path,
			String output_feature_defs_file_path)
	{
		try
		{
			// Make sure that all parameters are specified
			if (input_feature_vector_file_path_1 == null)
			{
				throw new Exception("Input feature vector file 1 file path not specified. Seven options must be present for this operation.");
			}
			if (input_feature_defs_file_path_1 == null)
			{
				throw new Exception("Input feature definitions file 1 file path not specified. Seven options must be present for this operation.");
			}
			if (input_feature_vector_file_path_2 == null)
			{
				throw new Exception("Input feature vector file 2 file path not specified. Seven options must be present for this operation.");
			}
			if (input_feature_defs_file_path_2 == null)
			{
				throw new Exception("Input feature definitions file 2 file path not specified. Seven options must be present for this operation.");
			}
			if (matching_key_file_path == null)
			{
				throw new Exception("Matching key file path not specified. Seven options must be present for this operation.");
			}
			if (output_feature_vector_file_path == null)
			{
				throw new Exception("Output feature vector file path not specified. Seven options must be present for this operation.");
			}
			if (output_feature_defs_file_path == null)
			{
				throw new Exception("Output feature definitions file path not specified. Seven options must be present for this operation.");
			}

			// Parse the matching key file
			File matching_key_file = null;
			try
			{
				matching_key_file = new File(matching_key_file_path);
			} catch (Exception e)
			{
				throw new Exception("Unable to access an input file at the specified path: " + matching_key_file_path);
			}
			String[] matching_key_lines_of_file = mckay.utilities.staticlibraries.FileMethods.parseTextFileLines(matching_key_file);
			String[][] matching_key_data = new String[matching_key_lines_of_file.length][];
			for (int line = 0; line < matching_key_lines_of_file.length; line++)
			{
				try
				{
					matching_key_data[line] = mckay.utilities.staticlibraries.StringMethods.breakIntoTokens(matching_key_lines_of_file[line], "\t");
				} catch (Exception e)
				{
					throw new Exception("Unable to parse the matching key file: " + matching_key_file_path + "\nProblem occured at line " + (line + 1) + ": " + matching_key_lines_of_file[line]);
				}
				if (matching_key_data[line].length != 3)
				{
					throw new Exception("Unable to parse the matching key file: " + matching_key_file_path + "\nThere must be 3 pieces of data on each line, separated by a tab, and this file has a line with " + matching_key_data[line].length + " pieces of data.\nProblem occured at line " + (line + 1) + ": " + matching_key_lines_of_file[line]);
				}
			}

			// Parse the two feature definition files
			FeatureDefinition[] feature_definitions_1 = null;
			try
			{
				feature_definitions_1 = ace.datatypes.FeatureDefinition.parseFeatureDefinitionsFile(input_feature_defs_file_path_1);
			} catch (Exception e)
			{
				throw new Exception("Unable to parse the first specified feature definitions file at the specified path: " + input_feature_defs_file_path_1 + "\n\nDETAILS:\n" + e.getMessage());
			}
			FeatureDefinition[] feature_definitions_2 = null;
			try
			{
				feature_definitions_2 = ace.datatypes.FeatureDefinition.parseFeatureDefinitionsFile(input_feature_defs_file_path_2);
			} catch (Exception e)
			{
				throw new Exception("Unable to parse second specified feature definitions file at the specified path: " + input_feature_defs_file_path_2 + "\n\nDETAILS:\n" + e.getMessage());
			}

			// Combine the two feature definition files
			FeatureDefinition[][] temp_definition_array =
			{
				feature_definitions_1, feature_definitions_2
			};
			FeatureDefinition[] feature_definitions_combined = ace.datatypes.FeatureDefinition.getMergedFeatureDefinitions(temp_definition_array);

			// Parse the two feature vector files
			DataSet[][] feature_vectors_array = new DataSet[2][];
			try
			{
				feature_vectors_array[0] = ace.datatypes.DataSet.parseDataSetFile(input_feature_vector_file_path_1, feature_definitions_1);
			} catch (Exception e)
			{
				throw new Exception("Unable to parse first specified feature values file at the specified path: " + input_feature_vector_file_path_1 + "\n\nDETAILS:\n" + e.getMessage());
			}
			try
			{
				feature_vectors_array[1] = ace.datatypes.DataSet.parseDataSetFile(input_feature_vector_file_path_2, feature_definitions_2);
			} catch (Exception e)
			{
				throw new Exception("Unable to parse second specified feature values file at the specified path: " + input_feature_vector_file_path_2 + "\n\nDETAILS:\n" + e.getMessage());
			}

			// Combine the feature vectos
			DataSet[] feature_vectors_combined = ace.datatypes.DataSet.getMergedFeatureTypes(feature_vectors_array, feature_definitions_combined, matching_key_data);

			// Save the combined feature definitions
			ace.datatypes.FeatureDefinition.saveFeatureDefinitions(feature_definitions_combined, new File(output_feature_defs_file_path), "Combined feature definitions of " + input_feature_defs_file_path_1 + " and " + input_feature_defs_file_path_2 + ".");

			// Save the combined feature vectors
			ace.datatypes.DataSet.saveDataSets(feature_vectors_combined, feature_definitions_combined, new File(output_feature_vector_file_path), "Combined feature vectors of " + input_feature_vector_file_path_1 + " and " + input_feature_vector_file_path_2 + ".");
		} // Output any errors to the command line
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
			// e.printStackTrace();
		}
	}


	/**
	 * Merge the instances contained in all ACE XML Feature Vector files in the directory_path
	 * directory and its sub-directories. The results are saved to a single ACE XML Feature Vector
	 * file specified by to_save_path. IMPORTANT: It is assumed that each instance is only referred
	 * to in a single one of the found files that are to be merged, as this may not merge features
	 * if an instance is found to occur more than once.
	 *
	 * @param directory_path	The directory to search for files to parse and merge.
	 * @param to_save_path		Where to save the merged results.
	 */
	private static void mergeInstances(String directory_path, String to_save_path)
	{
		// Do error checking
		try
		{
			if (directory_path == null)
				throw new Exception("No directory to search specified.");
			if ( !(new File(directory_path).exists()) )
				throw new Exception("The specified directory to search does not exist.");
			if (to_save_path == null)
				throw new Exception("No output file path specified.");
		}
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
			// e.printStackTrace();
			System.exit(0);
		}

		// Get all files with the (case-insensitive) .xml extension in the specified
		// directory and its sub-directories
		String[] accepted_extensions = {"xml"};
		FileFilter filter = new FileFilterImplementation(accepted_extensions);
		File directory = new File(directory_path);
		File[] files =  FileMethods.getAllFilesInDirectory(directory, true, filter, null);
		if (files == null)
		{
			System.out.println("No valid ACE XML Feature Vector files found in the specified directory and its sub-directories.");
			System.exit(0);
		}

		String[] file_names = new String[files.length];
		for (int i = 0; i < file_names.length; i++)
			file_names[i] = files[i].getAbsolutePath();

		// Construct a list of all valid Feature Vector files found
		Vector<String> valid_so_far = new Vector<String>();
		for (int i = 0; i < file_names.length; i++)
		{
			try
			{
				Object[] results = (Object[]) ace.xmlparsers.XMLDocumentParser.parseXMLDocument(file_names[i], "feature_vector_file");
				valid_so_far.add(file_names[i]);
			}
			catch (Exception e) {};
		}

		// Form an array of the valid files and notify the user of the files that will be joined
		String[] to_parse = valid_so_far.toArray(new String[1]);
		System.out.println("A total of " + to_parse.length + " valid ACE XML Feature Vector files were found in the specified directory and its sub-directories:");
		for (int i = 0; i < to_parse.length; i++)
			System.out.println("\t" + to_parse[i]);

		// Parse and join the ACE XML Feature Vector files
		DataSet[] instance_features = null;
		try {instance_features = DataSet.parseDataSetFiles(to_parse, null);}
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
			// e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Succesfully merged instances. Now saving results...");

		// Save the results
		try{DataSet.saveDataSets(instance_features, null, new File(to_save_path), "");}
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
			// e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Succesfully saved merged instances to " + to_save_path);
		System.out.println("EXECUTION COMPLETE");
	}


	/**
	 * Parse an iTunes XML file and output a text file where each line
	 * corresponds to a recording and the tab delimited entries for that line
	 * correspond to the field entries for the specified fields for the iTunes
	 * file.
	 *
	 * <p>Fields types may be "path", "title", "artist", "composer", "album"
	 * or "genre".
	 *
	 * <p>Errors are output to the command line.
	 *
	 * @param   input_file_path     The path of the iTunes XML file to parse.
	 * @param   output_file_path    The path of the text file to generate.
	 * @param   field_types         An array of field type identifiers.
	 */
	private static void matchItunesFields(String input_file_path,
			String output_file_path,
			String[] field_types)
	{
		// Set identifiers for iTunes fields
		final String FILE_PATH_ID = "path";
		final String TITLE_ID = "title";
		final String ARTIST_ID = "artist";
		final String COMPOSER_ID = "composer";
		final String ALBUM_ID = "album";
		final String GENRE_ID = "genre";

		try
		{
			// Do error checking and open the input file
			File input_file = null;
			if (input_file_path == null)
			{
				throw new Exception("No input file specified.");
			}
			if (output_file_path == null)
			{
				throw new Exception("No output file specified.");
			}
			if (field_types == null)
			{
				throw new Exception("No field types to parse specified.");
			}
			if (field_types.length > 6)
			{
				throw new Exception("You have specified " + field_types.length + " field types. The maximum is 6.");
			}

			// Parse the input file
			RecordingMetaData[] itunes_data = null;
			try
			{
				input_file = new File(input_file_path);
				itunes_data = RecordingMetaData.extractMetaDataFromiTunesXML(input_file, false);
			} catch (Exception e)
			{
				throw new Exception("Unable to open input file at the specified path: " + input_file_path);
			}

			// To hold the matched strings. The first dimension is the recording
			// and the second is the field.
			String[][] data_to_return = new String[itunes_data.length][field_types.length];

			// Extract the first field
			for (int recording = 0; recording < itunes_data.length; recording++)
			{
				for (int field = 0; field < field_types.length; field++)
				{
					if (field_types[field].equals(FILE_PATH_ID))
					{
						data_to_return[recording][field] = itunes_data[recording].file_path;
					}
					else
					{
						if (field_types[field].equals(TITLE_ID))
						{
							data_to_return[recording][field] = itunes_data[recording].title;
						}
						else
						{
							if (field_types[field].equals(ARTIST_ID))
							{
								data_to_return[recording][field] = itunes_data[recording].artist;
							}
							else
							{
								if (field_types[field].equals(COMPOSER_ID))
								{
									data_to_return[recording][field] = itunes_data[recording].composer;
								}
								else
								{
									if (field_types[field].equals(ALBUM_ID))
									{
										data_to_return[recording][field] = itunes_data[recording].album;
									}
									else
									{
										if (field_types[field].equals(GENRE_ID))
										{
											if (itunes_data[recording].genres != null)
											{
												data_to_return[recording][field] = "";
												for (int j = 0; j < itunes_data[recording].genres.length; j++)
												{
													if (j != 0)
													{
														data_to_return[recording][field] += " + ";
													}
													data_to_return[recording][field] += itunes_data[recording].genres[j];
												}
											}
										}
										else
										{
											throw new Exception("Field type of " + field_types[field] + " chosen. This is not a valid field type.\nFields types may be \"path\", \"title\", \"artist\", \"composer\", \"album\" or \"genre\".");
										}
									}
								}
							}
						}
					}
				}
			}

			// Write the extracted information
			try
			{
				// Prepare to write
				File output_file = mckay.utilities.staticlibraries.FileMethods.getNewFileForWriting(output_file_path, true);
				DataOutputStream writer = mckay.utilities.staticlibraries.FileMethods.getDataOutputStream(output_file);

				// Write lines one by one
				for (int recording = 0; recording < data_to_return.length; recording++)
				{
					for (int field = 0; field < field_types.length; field++)
					{
						// Insert the delimiting tab if appropriate
						if (field != 0)
						{
							writer.writeBytes("\t");
						}

						// Write the contents of this field
						if (data_to_return[recording][field] != null)
						{
							writer.writeBytes(data_to_return[recording][field]);
						}
					}

					// End the line
					if (recording != data_to_return.length - 1)
					{
						writer.writeBytes("\n");
					}
				}

				// Finish writing
				writer.close();
			} catch (Exception e)
			{
				throw new Exception("Unable to write to the specified output path: " + output_file_path);
			}
		} // Output any errors to the command line
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
		// e.printStackTrace();
		}
	}


	/**
	 * Parses two text files and generates a new text file based on a line by line merge of the two files.
	 * The two input files should have the same number of lines. Each line of the new text file will consist
	 * of a line from the first file followed by an inserted %%% followed by the matching line from the
	 * second text file. Spaces are replaced by underscores. The first command line entry following this
	 * flag must be the path of the first file to merge, the next must be the path of the second file to merge
	 * and the third must be the path of the file to generate.");
	 *
	 * @param first_input_file_path		The path of the first file to parse lines from.
	 * @param second_input_file_path	The path of the second file to parse lines from.
	 * @param output_file_path			The file path to write the ouput to.
	 */
	private static void generatePrimaryKeys(String first_input_file_path,
			String second_input_file_path,
			String output_file_path)
	{
		try
		{
			// Parse the first input file
			File first_input_file = null;
			try
			{
				first_input_file = new File(first_input_file_path);
			}
			catch (Exception e)
			{
				throw new Exception("Unable to access an input file at the specified path: " + first_input_file_path);
			}
			String[] lines_of_first_input_file = mckay.utilities.staticlibraries.FileMethods.parseTextFileLines(first_input_file);

			// Parse the first input file
			File second_input_file = null;
			try
			{
				second_input_file = new File(second_input_file_path);
			}
			catch (Exception e)
			{
				throw new Exception("Unable to access an input file at the specified path: " + second_input_file_path);
			}
			String[] lines_of_second_input_file = mckay.utilities.staticlibraries.FileMethods.parseTextFileLines(second_input_file);

			// Throw an exception if the number of lines do not match or if either is empty
			if (lines_of_first_input_file == null || lines_of_second_input_file == null)
				throw new Exception("One of the specified input files is empty.");
			if (lines_of_first_input_file.length != lines_of_second_input_file.length)
				throw new Exception("Input files have a differeing number of lines.");

			// Prepare the lines for the new file
			String[] output_lines = new String[lines_of_first_input_file.length];
			for (int i = 0; i < output_lines.length; i++)
			{
				output_lines[i] = lines_of_first_input_file[i] + "%%%" + lines_of_second_input_file[i];
				output_lines[i] = output_lines[i].replace(' ', '_');
			}

			// Write the extracted information
			try
			{
				// Prepare to write
				File output_file = mckay.utilities.staticlibraries.FileMethods.getNewFileForWriting(output_file_path, true);
				DataOutputStream writer = mckay.utilities.staticlibraries.FileMethods.getDataOutputStream(output_file);

				// Write lines one by one
				for (int i = 0; i < output_lines.length; i++)
				{
					if (i != 0) writer.writeBytes("\n");
					writer.writeBytes(output_lines[i]);
				}

				// Finish writing
				writer.close();
			}
			catch (Exception e)
			{
				throw new Exception("Unable to write to the specified output path: " + output_file_path);
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
			// e.printStackTrace();
		}
	}


	/**
	 * Parses an ACE XML Feature Values files as well as two text files. The fist text file must list the
	 * identifiers, one per line, of each instance in the Feature Values file in the same order as they appear
	 * in the Feature Values file (this is for the purpose of validation). The second text file consists of
	 * new identifiers, one per line. There must be a number of these equal to the number of instances, as
	 * the instances will have their identifiers replaced one by one with the new identifiers specified in the
	 * second text file, in the order that they appear. The new instances will be saved to a new Feature
	 * Values file at the specified path.
	 *
	 * @param feabure_values_file_path		Path to the original ACE XML Feature Values file.
	 * @param current_identifiers_file_path	Path to the text file containing the old instance identifiers.
	 * @param new_identifiers_file_path		Path to the text file containing the new instance identifiers.
	 * @param save_path						The path to save the new Feature Values file with the new
	 *										identifiers to.
	 */
	private static void modifyInstanceIdentifiers(String feabure_values_file_path,
			String current_identifiers_file_path,
			String new_identifiers_file_path,
			String save_path)
	{
		try
		{
			// Parse the current identifiers file
			File current_identifiers_file = null;
			try
			{
				current_identifiers_file = new File(current_identifiers_file_path);
			}
			catch (Exception e)
			{
				throw new Exception("Unable to access an input file at the specified path: " + feabure_values_file_path);
			}
			String[] current_identifiers = mckay.utilities.staticlibraries.FileMethods.parseTextFileLines(current_identifiers_file);

			// Parse the new identifiers input file
			File new_identifiers_file = null;
			try
			{
				new_identifiers_file = new File(new_identifiers_file_path);
			}
			catch (Exception e)
			{
				throw new Exception("Unable to access an input file at the specified path: " + new_identifiers_file_path);
			}
			String[] new_identifiers = mckay.utilities.staticlibraries.FileMethods.parseTextFileLines(new_identifiers_file);

			// Parse the ACE XML feature values file
			DataSet[] instances = DataSet.parseDataSetFile(feabure_values_file_path);

			// Throw an exception if the number of new and current identifiers do not match each other or
			// the number in instances
			if (new_identifiers.length != current_identifiers.length)
				throw new Exception("Differing number of new and current identifiers.");
			if (new_identifiers.length != instances.length)
				throw new Exception("Differing number of instances and specified identifiers.");

			// Replace the identifiers in the instances
			for (int i = 0; i < instances.length; i++)
			{
				if (!instances[i].identifier.equals(current_identifiers[i]))
					throw new Exception("The specified current identifier " + current_identifiers[i] + " does not match the actual identifier " + instances[i].identifier);
				if (new_identifiers[i] == null || new_identifiers[i].equals(""))
					throw new Exception("One of the new identifiers is unspecified.");
				instances[i].identifier = new_identifiers[i];
			}

			// Save the modified results
			DataSet.saveDataSets(instances, null, new File(save_path), "");
		}
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
			e.printStackTrace();
		}
	}


	/**
	 * Breaks the given ACE XML Feature Values file into one new ACE XML Features Values file for each feature
	 * that is contained in it. This new file only contains feature values for its associated feature, and
	 * only contains instances that have the given feature value available. Note that this does NOT yet
	 * including functionality for splitting on sectional features, and splitting only occurs for top-level
	 * features.
	 *
	 * @param input_feabure_values_file_path	The ACE XML Feature Value file to parse and split.
	 * @param output_folder						The name of a folder to store the output files in. The output
	 *											files will each be given the name of their corresponding
	 *											feature.
	 */
	private static void splitInstancesByFeatureType(String input_feabure_values_file_path,
			String output_folder)
	{
		try
		{
			// Prepare the save directory
			File save_directory = new File(output_folder);
			if (!save_directory.isDirectory())
				throw new Exception("The specified path " + output_folder + " does not refer to a directory.");

			// Parse the input ACE XML file
			DataSet[] original_instances = DataSet.parseDataSetFile(input_feabure_values_file_path);

			// Get all feature identifiers
			String[] feature_identifiers = DataSet.getFeatureNames(original_instances);
			feature_identifiers = mckay.utilities.staticlibraries.StringMethods.removeDoubles(feature_identifiers);

			// Save a Feature Values file for each feature, where instances only contain that feature
			for (int f = 0; f < feature_identifiers.length; f++)
			{
				// Instances with all features but the feature feature_identifiers[f] filtered out
				Vector<DataSet> filtered_instances = new Vector<DataSet>();

				// Look through the feature f in each of the original_instances and populate filtered_instances
				for (int i = 0; i < original_instances.length; i++)
				{
					// Find the current feature f in the instance i
					int feature_name_indice = -1;
					String[] these_feature_names = original_instances[i].feature_names;
					for (int fni = 0; fni < these_feature_names.length; fni++)
					{
						if (these_feature_names[fni].equals( feature_identifiers[f] ))
						{
							feature_name_indice = fni;
							fni = these_feature_names.length;
						}
					}

					// Store the filtered instance with only the feature corresponding to f in filtered_instances
					if (feature_name_indice != -1)
					{
						String[] new_feature_names = {feature_identifiers[f]};
						double[][] new_feature_values = {original_instances[i].feature_values[feature_name_indice]};
						DataSet filtered_insatnce = new DataSet(original_instances[i].identifier,
								original_instances[i].sub_sets,
								original_instances[i].start,
								original_instances[i].stop,
								new_feature_values,
								new_feature_names,
								original_instances[i].parent);
						filtered_instances.add(filtered_insatnce);
					}
				}

				// Save the filtered insances as ACE XML files
				if (!filtered_instances.isEmpty())
				{
					DataSet[] to_save = filtered_instances.toArray(new DataSet[filtered_instances.size()]);
					String save_path = save_directory.getAbsolutePath() + File.separator + feature_identifiers[f] + ".xml";
					DataSet.saveDataSets(to_save, null, new File(save_path), "Filtered by the feature " + feature_identifiers[f]);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR occured.");
			System.out.println(e.getMessage() + "\n");
			//e.printStackTrace();
		}
	}


	/**
	 * Prints the available choices of command line choices to standard out.
	 *
	 * @param command_line_error    Whether or not to print a message saying
	 *                              that invalid command line parameters were
	 *                              specified.
	 */
	private static void listOptions(boolean command_line_error)
	{
		if (command_line_error)
		{
			System.out.println("ERROR: Valid command line options were not provided.");
		}

		System.out.println("\nThis utility performs various utility tasks for the jMIR package, generally related to generating labelled ACE XML Classification Files, file parsing, file type conversions and combining existing ARFF or ACE XML files in various ways. It can only be run from the command line. The first command line argument must be a flag with a hyphen preceding it, and must be one of the choices listed below. Such a flag specifies the task to be performed. Some flags permit additional arguments afterwards, which do not require hyphens.");
		System.out.println("\nFILE CONVERSTION OPTIONS:");
		System.out.println("-convertFeatureValuesToCSV: Convert an ACE XML Feature Values file (and its associated Feature Definitions file) into a CSV file listing both instance identifiers and feature values. The -convertFeatureValuesToCSV option must be followed by two parameters, the first specifying path of the ACE XML Feature Values file to convert and the second specifying an ACE XML Feature Definitions file associted with it. The CSV file is given the same name as the Feature Values file, but with a CSV extention.");
		System.out.println("-convertFeatureValuesToARFF: Convert an ACE XML Feature Values file into a Weka ARFF file. Note that any existing file at the output path is overwritten. The instance identifiers stored in the ACE XML file are printed to standard out, as these cannot be stored in a meaningful way in Weka ARFF files. Also, the Weka relation name is defaulted to \"Converted_from_ACE_XML\". This option must be followed by two parameters, the first specifying path of the ACE XML Feature Values file to convert, and the second specifying the path of the new Weka ARFF file to save to.");
		System.out.println("\nLABELLING OPTIONS:");
		System.out.println("-batchAudioIdentify: Identify all MP3 files in a specified directory and its sub-directories based on audio fingerprinting, and save the results in a tab-delimitted text file (specifying file path, title and artist for one file per line). This option must be followed by three parameters, the first specifying the audio root directory, the second specifying the output file path and the thrid specifying an Echo Nest API Key (to enable access to Echo Nest fingerprinting web services.");
		System.out.println("-fileLabelInstances: Run a GUI for generating an ACE XML Classifications File to contain model labels where instances are identified based on file paths batch selected by the user, and assigned class names are batch entered by the user.");
		System.out.println("-txtLabelInstances: Generate a model classifications ACE XML Classifications File based on a tab delimited text file. Each instance corresponds to a line in the text file. The first entry on the line is the name of the instance and the second (or more) are the name(s) of its class(es). The first command line option must specify the input file path of the tab delimited text file and the second must specify the output file path of the ACE XML file to be saved.");
		System.out.println("-modifyInstanceIdentifiers: Parse an ACE XML Feature Values files as well as two text files. The fist text file must list the identifiers, one per line, of each instance in the Feature Values file in the same order as they appear in the Feature Values file (this is for the purpose of validation). The second text file consists of new identifiers, one per line. There must be a number of these equal to the number of instances, as the instances will have their identifiers replaced one by one with the new identifiers specified in the second text file, in the order that they appear. The new instances will be saved to a new Feature Values file at the specified path.");
		System.out.println("\nMERGE OPTIONS:");
		System.out.println("-mergeFeatures: Merge the extracted feature contained in two different ACE XML Feature Vector files and their corresponding Feature Definition Files. The two input Feature Vector files should refer to different feature extracted for the same instances. This could be useful, for example, for combining features extracted from jAudio with features extracted with jWebMiner for the same songs. There should be seven options provided at the command line, each referring to a file path: 1) The first input feature vector file. 2) The feature definitions file for this first feature vector file. 3) The second input feature vector file. 4) The feature definitions file for this second feature vector file. 5) A tab delimited text file, such as one that might be output by the -matchItunesFields flag, where each line corresponds to an instance and each line has three pieces of information separated by tabs, namely the instance identifier for the fist feature vector file, the corresponding instance identifier for the second feature vector file, and the instance identifier to use for the output file. 6) The file path to save the combined feature vector file to. 7) The file path to save the combined feature definitions file to. Note that this cannot yet deal with datasets that have subsets (e.g. features separately extracted separately for windows), and such subsets are currently just ignored.");
		System.out.println("-mergeInstances: Merge the instances contained in all ACE XML Feature Vector files in the specified directory and its sub-directories. The results are saved to a single ACE XML Feature Vector file. There should be two options provided at the command line, the first specifying the path of the directory to search and the second specifying the path to save to.");
		System.out.println("-generatePrimaryKeys: Parse two text files and generate a new text file based on a line by line merge of the two files. The two input files should have the same number of lines. Each line of the new text file will consist of a line from the first file followed by an inserted %%% followed by the matching line from the second text file. Spaces are replaced by underscores. The first command line entry following this flag must be the path of the first file to merge, the next must be the path of the second file to merge and the third must be the path of the file to generate.");
		System.out.println("-splitInstancesByFeatureType: Break the given ACE XML Feature Values file into one new ACE XML Features Values file for each feature that is contained in it. This new file only contains feature values for its associated feature, and only contains instances that have the given feature value available. Note that this does NOT yet including functionality for splitting on sectional features, and splitting only occurs for top-level features.");
		System.out.println("\nFILE PARSING OPTIONS:");
		System.out.println("-matchItunesFields: Parse an iTunes XML file and output a text file where each line corresponds to a recording and the tab delimited entries for that line correspond to the field entries for the specified fields for the iTunes file. Fields types may be \"path\", \"title\", \"artist\", \"composer\", \"album\" or \"genre\". Three to eight command line options must follow this flag. The first must be the input file path of the iTunes XML file, the second must be the output file path of the text file to be saved, and the remainder specify the field type(s) and in what order they are to be listed in the text file.");
		System.out.println("\nHELP OPTIONS:");
		System.out.println("-help: List the command line interface options.");
		System.out.println("\n");
	}
}