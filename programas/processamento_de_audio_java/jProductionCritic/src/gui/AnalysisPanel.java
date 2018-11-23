/**
 * AnalysisPanel.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package gui;

import jproductioncritic.JProductionCritic;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * <p>The AnalysisPanel is used for controlling the reports that are generated when audio files are analyzed 
 * for technical production errors. It initiates processing and displays the results.</p>
 * 
 * <p>The top section of the Analysis Pane allows the user to select the error reports that are to be 
 * generated, and where to save them. The checkbox for a type of report must be selected before its save path
 * can be specified. Report paths can be entered directly in the text fields, or via a browse file chooser 
 * dialog box. Note that sometimes it is appropriate to specify single files and sometimes it is appropriate 
 * to specify directories in the report paths, depending on the type of report to be generated and on whether 
 * a single selected file is to checked or the contents of a directory (i.e. which button at the bottom of the
 * pane is pressed). In particular, note that automatically generated Audacity label track file names are 
 * given the .txt extension, which could cause them to overwrite .txt report files set to be generated in the 
 * same directory, so it is wise to save text file reports and Audacity label track reports in separate
 * directories or with different names.</p>
 * 
 * <p>The middle section of the Analysis Pane has two parts: the Status Updates and Command Line Reports area
 * on the left, and the Problems Occurring During Processing area on the right. The Status Updates and Command
 * Line Reports area displays processing status reports output by jProductionCritic as files are being checked
 * for errors. It also displays Command Line Reports if the checkbox for this report type is checked above. 
 * The Problems Occurring During Processing area indicates details of any problems that may occur during
 * processing (e.g. invalid files chosen for analysis, inappropriate configuration settings chosen by the 
 * user, etc.).</p>
 * 
 * <p>Finally, the buttons on the bottom of the screen allow the user to initiate error checking of audio
 * files for production errors. The Check Single File button brings up a dialog box allowing the user to 
 * select a single file for error checking, and the Check Contents of Directory button brings up a dialog box 
 * allowing the user to select a directory whose contents will be checked for production errors (the contents
 * of sub-directories are not examined).</p>
 * 
 * @author	Cory McKay
 */
public class AnalysisPanel
	extends JPanel
	implements ActionListener
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The OuterFrame whose tabbed pane holds this JPanel.
	 */
	private	OuterFrame			outer_frame;
	
	/**
	 * The stream to which processing status updates and command line reports are written to. Assigned to the
	 * status_text_area.
	 */
	private PrintStream			status_stream;
	
	/**
	 * The stream to which problems reports corresponding to errors encountered during processing are written
	 * to. Assigned to the error_text_area.
	 */
	private	PrintStream			error_stream;
	
	/**
	 * The text area to which processing status updates and command line reports are written to.
	 */
	private	JTextArea			status_text_area;
	
	/**
	 * The text area to which problems reports corresponding to errors encountered during processing are
	 * written to.
	 */
	private	JTextArea			error_text_area;
	
	/**
	 * The check box indicating whether or not a command line type reports should be generated and displayed.
	 */	
	private	JCheckBox			command_line_report_checkbox;
	
	/**
	 * The check box indicating whether or not text file reports should be generated and saved.
	 */	
	private	JCheckBox			text_report_checkbox;
	
	/**
	 * The check box indicating whether or not HTML reports should be generated and saved.
	 */	
	private	JCheckBox			html_report_checkbox;
	
	/**
	 * The check box indicating whether or not Audacity label track reports should be generated and saved.
	 */	
	private	JCheckBox			audacity_report_checkbox;
	
	/**
	 * The check box indicating whether or not an ACE XML report should be generated and saved.
	 */	
	private	JCheckBox			ace_xml_report_checkbox;
	
	/**
	 * The check box indicating whether or not Weka ARFF reports should be generated and saved.
	 */	
	private	JCheckBox			weka_arff_report_checkbox;
		
	/**
	 * Holds the path to save one or more text file reports to.
	 */
	private	JTextField			text_report_path_textfield;
	
	/**
	 * Holds the path to save one or more HTML reports to.
	 */
	private	JTextField			html_report_path_textfield;
	
	/**
	 * Holds the path to save one or more Audacity label track reports to.
	 */
	private	JTextField			audacity_report_path_textfield;
	
	/**
	 * Holds the path to save an ACE XML report to.
	 */
	private	JTextField			ace_xml_report_path_textfield;
	
	/**
	 * Holds the path to save one or more Weka ARFF reports to.
	 */
	private	JTextField			weka_arff_report_path_textfield;
	
	/**
	 * Button to bring up a file chooser for selecting the path to save text file reports to. Sets the
	 * corresponding report path text field to the selection made.
	 */
	private	JButton				text_report_browse_button;
	
	/**
	 * Button to bring up a file chooser for selecting the path to save HTML reports to. Sets the
	 * corresponding report path text field to the selection made.
	 */
	private	JButton				html_report_browse_button;
	
	/**
	 * Button to bring up a file chooser for selecting the path to save Audacity label track reports to. Sets
	 * the corresponding report path text field to the selection made.
	 */
	private	JButton				audacity_report_browse_button;
	
	/**
	 * Button to bring up a file chooser for selecting the path to save the ACE XML report to. Sets the
	 * corresponding report path text field to the selection made.
	 */
	private	JButton				ace_xml_report_browse_button;
	
	/**
	 * Button to bring up a file chooser for selecting the path to save Weka ARFF reports to. Sets the
	 * corresponding report path text field to the selection made.
	 */
	private	JButton				weka_arff_report_browse_button;
	
	/**
	 * The dialog box used to choose various load and save paths.
	 */
	private JFileChooser		path_browser_dialog;
	
	/**
	 * The button that begins error checking of a single chosen file.
	 */
	private JButton				single_file_check_button;
	
	/**
	 * The button that begins error checking of the contents of a chosen directory.
	 */
	private	JButton				batch_check_button;
	
	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Sets up and displays the AnalysisPanel GUI component.
	 * 
	 * @param	outer_frame	The OuterFrame whose tabbed pane holds this JPanel.
	 */
	public AnalysisPanel(OuterFrame outer_frame)
	{
		// Store the GUI references
		this.outer_frame = outer_frame;

		// Set border gaps
		int horizontal_gap = 4;
		int vertical_gap = 4;
		
		// Set up path_browser_dialog
		path_browser_dialog = new JFileChooser();
		path_browser_dialog.setCurrentDirectory(new File("."));
		
		// Set up the report check boxes
		JPanel report_checkbox_panel = new JPanel(new GridLayout(7, 1));
		command_line_report_checkbox = new JCheckBox("Command Line");
		text_report_checkbox = new JCheckBox("Text File");
		text_report_checkbox.addActionListener(AnalysisPanel.this);		
		html_report_checkbox = new JCheckBox("HTML");
		html_report_checkbox.addActionListener(AnalysisPanel.this);		
		audacity_report_checkbox = new JCheckBox("Audacity Label Track");
		audacity_report_checkbox.addActionListener(AnalysisPanel.this);		
		ace_xml_report_checkbox = new JCheckBox("ACE XML");
		ace_xml_report_checkbox.addActionListener(AnalysisPanel.this);		
		weka_arff_report_checkbox = new JCheckBox("Weka ARFF");
		weka_arff_report_checkbox.addActionListener(AnalysisPanel.this);		
		command_line_report_checkbox.setSelected(true);
		text_report_checkbox.setSelected(false);
		html_report_checkbox.setSelected(false);
		audacity_report_checkbox.setSelected(false);
		ace_xml_report_checkbox.setSelected(false);
		weka_arff_report_checkbox.setSelected(false);
		JLabel report_checkbox_panel_label = new JLabel("REPORTS TO MAKE:");
		formatLabel(report_checkbox_panel_label);
		report_checkbox_panel.add(report_checkbox_panel_label);
		report_checkbox_panel.add(text_report_checkbox);
		report_checkbox_panel.add(html_report_checkbox);
		report_checkbox_panel.add(audacity_report_checkbox);
		report_checkbox_panel.add(ace_xml_report_checkbox);
		report_checkbox_panel.add(weka_arff_report_checkbox);
		report_checkbox_panel.add(command_line_report_checkbox);
	
		// Set up the report path text fields
		JPanel report_textfield_panel = new JPanel(new GridLayout(7, 1));
		text_report_path_textfield = new JTextField();
		html_report_path_textfield = new JTextField();
		audacity_report_path_textfield = new JTextField();
		ace_xml_report_path_textfield = new JTextField();
		weka_arff_report_path_textfield = new JTextField();
		JLabel report_textfield_panel_label = new JLabel("PATHS TO SAVE REPORTS TO:");
		formatLabel(report_textfield_panel_label);
		report_textfield_panel.add(report_textfield_panel_label);
		report_textfield_panel.add(text_report_path_textfield);
		report_textfield_panel.add(html_report_path_textfield);
		report_textfield_panel.add(audacity_report_path_textfield);
		report_textfield_panel.add(ace_xml_report_path_textfield);
		report_textfield_panel.add(weka_arff_report_path_textfield);
		report_textfield_panel.add(new JLabel());
		
		// Set up the report path browse buttons
		JPanel report_browse_button_panel = new JPanel(new GridLayout(7, 1));
		text_report_browse_button = new JButton("Browse");
		text_report_browse_button.addActionListener(AnalysisPanel.this);		
		html_report_browse_button = new JButton("Browse");
		html_report_browse_button.addActionListener(AnalysisPanel.this);		
		audacity_report_browse_button = new JButton("Browse");
		audacity_report_browse_button.addActionListener(AnalysisPanel.this);		
		ace_xml_report_browse_button = new JButton("Browse");
		ace_xml_report_browse_button.addActionListener(AnalysisPanel.this);		
		weka_arff_report_browse_button = new JButton("Browse");
		weka_arff_report_browse_button.addActionListener(AnalysisPanel.this);		
		report_browse_button_panel.add(new JLabel());
		report_browse_button_panel.add(text_report_browse_button);
		report_browse_button_panel.add(html_report_browse_button);
		report_browse_button_panel.add(audacity_report_browse_button);
		report_browse_button_panel.add(ace_xml_report_browse_button);
		report_browse_button_panel.add(weka_arff_report_browse_button);		
		report_browse_button_panel.add(new JLabel());
	
		// Set up the overall report selection panel
		JPanel report_selection_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		report_selection_panel.add(report_checkbox_panel, BorderLayout.WEST);
		report_selection_panel.add(report_textfield_panel, BorderLayout.CENTER);
		report_selection_panel.add(report_browse_button_panel, BorderLayout.EAST);
		
		// Set the GUI based on checkbox values
		updateBasedOnCheckBoxes();
		
		// Set up the status output panel
		JPanel status_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		status_text_area = new JTextArea();
		status_text_area.setLineWrap(false);
		status_text_area.setEditable(false);
		status_text_area.setTabSize(2);
		status_text_area.setText("");
		JLabel status_panel_label = new JLabel("STATUS UPDATES AND COMMAND LINE REPORTS:");
		formatLabel(status_panel_label);
		status_panel.add(status_panel_label, BorderLayout.NORTH);
		status_panel.add(new JScrollPane(status_text_area), BorderLayout.CENTER);
		
		// Set up the error output panel
		JPanel error_panel = new JPanel(new BorderLayout(horizontal_gap, vertical_gap));
		error_text_area = new JTextArea();
		error_text_area.setLineWrap(true);
		error_text_area.setEditable(false);
		error_text_area.setTabSize(2);
		error_text_area.setForeground(Color.RED);
		error_text_area.setText("");
		JLabel error_panel_label = new JLabel("PROBLEMS OCCURING DURING PROCESSING:");
		formatLabel(error_panel_label);
		error_panel.add(error_panel_label, BorderLayout.NORTH);
		error_panel.add(new JScrollPane(error_text_area), BorderLayout.CENTER);

		// Set up the overall text areas panel
		JPanel text_areas_panel = new JPanel(new GridLayout(1, 2));
		text_areas_panel.add(status_panel);
		text_areas_panel.add(error_panel);
		
		// Set the text area print streams
		status_stream = getPrintStream(status_text_area);
		error_stream = getPrintStream(error_text_area);
		
		// Set up the analysis execution buttons
		JPanel execution_button_panel = new JPanel(new GridLayout(1, 2));
		single_file_check_button = new JButton("CHECK SINGLE FILE");
		single_file_check_button.addActionListener(AnalysisPanel.this);
		batch_check_button = new JButton("CHECK CONTENTS OF DIRECTORY");
		batch_check_button.addActionListener(AnalysisPanel.this);
		execution_button_panel.add(single_file_check_button);
		execution_button_panel.add(batch_check_button);

		// Prepare the overall layout
		setLayout(new BorderLayout(horizontal_gap, vertical_gap));
		add(new JScrollPane(report_selection_panel), BorderLayout.NORTH);
		add(text_areas_panel, BorderLayout.CENTER);
		add(execution_button_panel, BorderLayout.SOUTH);
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * Returns the PrintStream that writes to the error_text_area.
	 * 
	 * @return The PrintStream that writes to the error_text_area.
	 */
	public PrintStream getErrorStream()
	{
		return error_stream;
	}
	
	/**
	 * Perform appropriate actions in response to GUI interactions.
	 *
	 * @param	event	The event that is to be reacted to.
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		// React to the single_file_check_button
		if (event.getSource().equals(single_file_check_button))
		{
			Thread thread = new Thread( new SingleFileProcessingThread() );
			thread.start();
		}
	
		// React to the batch_check_button
		if (event.getSource().equals(batch_check_button))
		{
			Thread thread = new Thread( new BatchProcessingThread() );
			thread.start();
		}

		// React to the text_report_browse_button
		if (event.getSource().equals(text_report_browse_button))
		{
			path_browser_dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			path_browser_dialog.setDialogTitle("Choose Text Report Save Path");
		
			if (path_browser_dialog.showSaveDialog(AnalysisPanel.this) == JFileChooser.APPROVE_OPTION)
			{
				String selected_path = path_browser_dialog.getSelectedFile().getAbsolutePath();
				text_report_path_textfield.setText(selected_path);
			}
		}		
		
		// React to the html_report_browse_button
		if (event.getSource().equals(html_report_browse_button))
		{
			path_browser_dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			path_browser_dialog.setDialogTitle("Choose HTML Report Save Path");
		
			if (path_browser_dialog.showSaveDialog(AnalysisPanel.this) == JFileChooser.APPROVE_OPTION)
			{
				String selected_path = path_browser_dialog.getSelectedFile().getAbsolutePath();
				html_report_path_textfield.setText(selected_path);
			}
		}		
		
		// React to the audacity_report_browse_button
		if (event.getSource().equals(audacity_report_browse_button))
		{
			path_browser_dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			path_browser_dialog.setDialogTitle("Choose Audacity Label Track Save Path");
		
			if (path_browser_dialog.showSaveDialog(AnalysisPanel.this) == JFileChooser.APPROVE_OPTION)
			{
				String selected_path = path_browser_dialog.getSelectedFile().getAbsolutePath();
				audacity_report_path_textfield.setText(selected_path);
			}
		}		
		
		// React to the ace_xml_report_browse_button
		if (event.getSource().equals(ace_xml_report_browse_button))
		{
			path_browser_dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
			path_browser_dialog.setDialogTitle("Choose ACE XML Save Path");
			if (path_browser_dialog.showSaveDialog(AnalysisPanel.this) == JFileChooser.APPROVE_OPTION)
			{
				String selected_path = path_browser_dialog.getSelectedFile().getAbsolutePath();
				ace_xml_report_path_textfield.setText(selected_path);
			}
		}		
		
		// React to the weka_arff_report_browse_button
		if (event.getSource().equals(weka_arff_report_browse_button))
		{
			path_browser_dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			path_browser_dialog.setDialogTitle("Choose Weka ARFF Save Path");
		
			if (path_browser_dialog.showSaveDialog(AnalysisPanel.this) == JFileChooser.APPROVE_OPTION)
			{
				String selected_path = path_browser_dialog.getSelectedFile().getAbsolutePath();
				weka_arff_report_path_textfield.setText(selected_path);
			}
		}

		// React to any of the report check boxes being selected or deselected
		if ( event.getSource().equals(text_report_checkbox) || 
		     event.getSource().equals(html_report_checkbox) ||
			 event.getSource().equals(audacity_report_checkbox) ||
			 event.getSource().equals(ace_xml_report_checkbox) ||
			 event.getSource().equals(weka_arff_report_checkbox) )
		{
			updateBasedOnCheckBoxes();
		}
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Adjust the formatting of the given JLabel.
	 * 
	 * @param to_format	The JLabel to format.
	 */
	private static void formatLabel (JLabel to_format)
	{
		// Change the font, style and/or size
		Font old_font = to_format.getFont();
		Font new_font = new Font(old_font.getFontName(), Font.BOLD, (old_font.getSize() + 1));
		to_format.setFont(new_font);
		
		// Change the alignment
		to_format.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Change the colour
		to_format.setForeground(Color.DARK_GRAY);
	}
	
	/**
	 * Enable or disable GUI components based on the value of the report generation check boxes.
	 */
	private void updateBasedOnCheckBoxes()
	{
			if (text_report_checkbox.isSelected())
			{
				text_report_path_textfield.setEnabled(true);
				text_report_browse_button.setEnabled(true);
			}
			else
			{
				text_report_path_textfield.setEnabled(false);
				text_report_browse_button.setEnabled(false);
			}
			
			if (html_report_checkbox.isSelected())
			{
				html_report_path_textfield.setEnabled(true);
				html_report_browse_button.setEnabled(true);
			}
			else
			{
				html_report_path_textfield.setEnabled(false);
				html_report_browse_button.setEnabled(false);
			}
			
			if (audacity_report_checkbox.isSelected())
			{
				audacity_report_path_textfield.setEnabled(true);
				audacity_report_browse_button.setEnabled(true);
			}
			else
			{
				audacity_report_path_textfield.setEnabled(false);
				audacity_report_browse_button.setEnabled(false);
			}
			
			if (ace_xml_report_checkbox.isSelected())
			{
				ace_xml_report_path_textfield.setEnabled(true);
				ace_xml_report_browse_button.setEnabled(true);
			}
			else
			{
				ace_xml_report_path_textfield.setEnabled(false);
				ace_xml_report_browse_button.setEnabled(false);
			}
			
			if (weka_arff_report_checkbox.isSelected())
			{
				weka_arff_report_path_textfield.setEnabled(true);
				weka_arff_report_browse_button.setEnabled(true);
			}
			else
			{
				weka_arff_report_path_textfield.setEnabled(false);
				weka_arff_report_browse_button.setEnabled(false);
			}
	}
	
	/**
	 * Delete all contents in the status_text_area and error_text_area.
	 */
	private void clearTextAreas()
	{
		status_text_area.setText("");
		error_text_area.setText("");
	}
	
	/**
	 * Return a PrintStream that, when written to, will append the supplied text to the specified JTextArea.
	 * Note that focus is given to this AnalysisPanel in its OuterFrame tabbed pane whenever this PrintStream
	 * is written to.
	 * 
	 * @param text_area	The JTextArea to append text to when the returned PrintStream is written to.
	 * @return			The PrintStream that can be used to access text_area.
	 */
	private PrintStream getPrintStream(final JTextArea text_area)
	{
		OutputStream new_stream = new OutputStream() 
		{
			@Override
			public void write(int b) throws IOException 
			{
				appendToTextArea(String.valueOf((char) b), text_area);
				outer_frame.setFocusToAnalysisPanel();
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException 
			{
				appendToTextArea(new String(b, off, len), text_area);
				outer_frame.setFocusToAnalysisPanel();
			}

			@Override
			public void write(byte[] b)
				throws IOException
			{
				write(b, 0, b.length);
			}
		};
		
		return new PrintStream(new_stream);
	}
	
	/**
	 * Append the given text to the given text_area.
	 * 
	 * @param text		The text to append.
	 * @param text_area The JTextArea to append text to.
	 */
	private static void appendToTextArea(final String text, final JTextArea text_area)
	{
		SwingUtilities.invokeLater( new Runnable() {@Override public void run() {text_area.append(text);}} );
	}
		
	/**
	 * Construct the portion of the jProductionCritic command line associated with output reports and with the
	 * configuration file, based on the GUI settings.
	 * 
	 * @return	A set of flags and values formatted for the jProductionCritic command line.
	 */
	private ArrayList<String> getCommandLineArgumentsSaveReportAndConfigsPortion()
	{
		// To store the command line arguments
		ArrayList<String> arguments = new ArrayList<>();
		
		// Set the report type portion
		if (command_line_report_checkbox.isSelected())
		{
			arguments.add("-reportcmdline");
			arguments.add("yes");
		}
		if (text_report_checkbox.isSelected())
		{
			arguments.add("-reporttxt");
			arguments.add(text_report_path_textfield.getText());
		}
		if (html_report_checkbox.isSelected())
		{
			arguments.add("-reporthtml");
			arguments.add(html_report_path_textfield.getText());
		}
		if (audacity_report_checkbox.isSelected())
		{
			arguments.add("-reportaudacity");
			arguments.add(audacity_report_path_textfield.getText());
		}
		if (ace_xml_report_checkbox.isSelected())
		{
			arguments.add("-reportacexml");
			arguments.add(ace_xml_report_path_textfield.getText());
		}
		if (weka_arff_report_checkbox.isSelected())
		{
			arguments.add("-reportwekaarff");
			arguments.add(weka_arff_report_path_textfield.getText());
		}
		
		// Set the configuration file portion
		arguments.add("-configfile");
		arguments.add(outer_frame.getConfigFilePath());

		// Return the results
		return arguments;		
	}
	
	/**
	 * Print an explanatory message to the status stream indicating the command line arguments used to run
	 * jProductionCritic.
	 * 
	 * @param to_print	The command line arguments.
	 */
	private void printCommandLineArgumentsToStatusStream(String[] to_print)
	{
		status_stream.print(">>> Running based on the following command line arguments: ");
		for (int i = 0; i < to_print.length; i++)
			status_stream.print(to_print[i] + " ");
		status_stream.print("\n\n");
	}
	
	/**
	 * Verify that the user's GUI settings on this AnalysisPanel do not violate the command line logic 
	 * requirements.
	 * 
	 * @return	Null if the GUI settings are valid, informative messages if they are not.
	 */
	private String validateGUISettings()
	{
		String error_messages = null;
		
		if ( !command_line_report_checkbox.isSelected() && !text_report_checkbox.isSelected() &&
		     !html_report_checkbox.isSelected() && !audacity_report_checkbox.isSelected() &&
			 !ace_xml_report_checkbox.isSelected() && !weka_arff_report_checkbox.isSelected() )
		{
			if (error_messages == null)
				error_messages = "";
			error_messages += "No reports are selected to be generated. At least one report type must be selected for generation in order for processing to proceed.\n";
		}
		
		return error_messages;
	}
	

	/* INTERNAL CLASSES *************************************************************************************/


	/**
	 * Select the audio file to check for errors using a file chooser dialog box and perform processing
	 * corresponding to the -check command line run option. It is necessary to do this in a thread in order to
	 * ensure that text area updates happen during processing rather than at the end of processing.
	 * As part of this processing, the contents of the settings table in the Settings Panel are saved to the
	 * file referred to in the Configuration Settings File text field.
	 */
	private class SingleFileProcessingThread
		implements Runnable
	{
		@Override
		public void run()
		{
			// Validate the logic of the GUI settings
			String error_messages = validateGUISettings();
			
			// If the AnalysisPanel GUI setting logic is correct
			if (error_messages == null)
			{
				// Configure the file chooser dialog box
				path_browser_dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
				path_browser_dialog.setDialogTitle("Choose Audio File to Check for Errors");

				if (path_browser_dialog.showOpenDialog(AnalysisPanel.this) == JFileChooser.APPROVE_OPTION)
				{
					// Delete all content displayed in both text areas
					clearTextAreas();

					// Save the entered configuration settings to the appropriate file
					status_stream.println(">>> Saving configuration settings . . .\n");
					boolean save_worked = outer_frame.saveConfigurationSettings();
					
					if (save_worked)
					{
						// Construct the part of the command line associated with report generation
						ArrayList<String> save_report_arguments = getCommandLineArgumentsSaveReportAndConfigsPortion();

						// The path of the audio file to analyze
						String selected_path = path_browser_dialog.getSelectedFile().getAbsolutePath();

						// Add the analysis flag and value to the command line arguments
						save_report_arguments.add("-check");
						save_report_arguments.add(selected_path);

						// Finalize the command line arguments
						String[] command_line_arguments = save_report_arguments.toArray( new String[save_report_arguments.size()] );

						// Print the command line arguments to the status stream
						printCommandLineArgumentsToStatusStream(command_line_arguments);
		
						// Perform all processing and report saving				
						JProductionCritic.performAllProcessingBasedOnCommandLineArguements(command_line_arguments, status_stream, error_stream);
					}
				}		
			}
			
			// If the AnalysisPanel GUI setting logic is incorrect
			else
			{
				error_stream.append(error_messages);
				error_stream.append("\nEXECUTION TERMINATED DUE TO INVALID COMMAND LINE ARGUMENTS\n\n");
			}
			
			// Play a standard beep when processing is complete
			java.awt.Toolkit.getDefaultToolkit().beep();
		}
	}
	
	/**
	 * Select the directory to check for errors using a file chooser dialog box and perform processing
	 * corresponding to the -batchcheck command line run option. It is necessary to do this in a thread in 
	 * order to ensure that text area updates happen during processing rather than at the end of processing.
	 * As part of this processing, the contents of the settings table in the Settings Panel are saved to the
	 * file referred to in the Configuration Settings File text field.
	 */
	private class BatchProcessingThread
		implements Runnable
	{
		@Override
		public void run()
		{
			// Validate the logic of the GUI settings
			String error_messages = validateGUISettings();
			
			// If the AnalysisPanel GUI setting logic is correct
			if (error_messages == null)
			{			
				// Configure the file chooser dialog box
				path_browser_dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				path_browser_dialog.setDialogTitle("Choose Directory to Load Audio Files From");

				if (path_browser_dialog.showOpenDialog(AnalysisPanel.this) == JFileChooser.APPROVE_OPTION)
				{
					// Delete all content displayed in both text areas
					clearTextAreas();

					// Save the entered configuration settings to the appropriate file
					status_stream.println(">>> Saving configuration settings . . .\n");
					boolean save_worked = outer_frame.saveConfigurationSettings();
					
					if (save_worked)
					{
						// Construct the part of the command line associated with report generation
						ArrayList<String> save_report_arguments = getCommandLineArgumentsSaveReportAndConfigsPortion();

						// The path of the directory containing the audio files to analyze
						String selected_path = path_browser_dialog.getSelectedFile().getAbsolutePath();

						// Add the analysis flag and value to the command line arguments
						save_report_arguments.add("-batchcheck");
						save_report_arguments.add(selected_path);

						// Finalize the command line arguments
						String[] command_line_arguments = save_report_arguments.toArray( new String[save_report_arguments.size()] );

						// Print the command line arguments to the status stream
						printCommandLineArgumentsToStatusStream(command_line_arguments);

						// Perform all processing and report saving				
						JProductionCritic.performAllProcessingBasedOnCommandLineArguements(command_line_arguments, status_stream, error_stream);
					}
				}	
			}
			
			// If the AnalysisPanel GUI setting logic is incorrect
			else
			{
				error_stream.append(error_messages);
				error_stream.append("\nEXECUTION TERMINATED DUE TO INVALID COMMAND LINE ARGUMENTS\n\n");
			}
			
			// Play a standard beep when processing is complete
			java.awt.Toolkit.getDefaultToolkit().beep();
		}
	}
}