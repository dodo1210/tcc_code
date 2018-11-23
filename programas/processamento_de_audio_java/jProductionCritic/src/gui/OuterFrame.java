/**
 * OuterFrame.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package gui;

import mckay.utilities.gui.templates.AboutDialog;
import mckay.utilities.gui.templates.HelpDialog;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

/**
 * <p>The container holding the jProductionCritic GUI. Holds the AnalysisPane and SettingsPane as well as the 
 * GUI's menu items. Also provides methods for accessing functionality offered by the AnalysisPane and 
 * SettingsPane.</p>
 * 
 * <p>The jProductionCritic GUI consists of two primary panes: a Configuration Settings Pane for selecting
 * configuration settings governing which production errors are checked for and using what thresholds, and an
 * Analysis Pane for controlling the reports that are generated, displaying the results of processing and 
 * initiating processing. There are also two menu commands that can be accessed by the user:</p>
 * 
 * <ul> <li><strong>About:</strong> Brings up a dialog box displaying basic jProductionCritic version 
 * data.</li>
 * <li><strong>Help: </strong>Brings up a window containing the jProductionCritic manual. Different sections 
 * can be jumped to using the pane on the left.</li></ul>
 * 
 * <p>It should be noted that the GUI is simply a front end for jProductionCritic, which in essence simply 
 * allows users to enter settings which are then formulated by the GUI as command line arguments, and sent to
 * the backend to be run as if only command line arguments were used. The processing sequence is therefore
 * identical to that executed when using the command line interface. The only functionality that can be 
 * performed with the GUI that cannot be performed via command line arguments is the editing of the 
 * configuration settings, and this is easy enough to do with a text editor for those who prefer to bypass the
 * GUI and use jProductionCritic directly with its command line argument interface.</p>
 * 
 * <p>The GUI is run by calling the startGUI method after instantiating an object of this class.</p>
 * 
 * @author	Cory McKay
 */
public class OuterFrame
	extends JFrame
	implements ActionListener
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * Contains the interface elements for error checking I/O.
	 */
	private	AnalysisPanel	analysis_panel;
	
	/**
	 * Contains the interface elements for setting error checking configuration settings.
	 */	
	private	SettingsPanel	settings_panel;

	/**
	 * Holds the main interface panes.
	 */	
	private JTabbedPane		tabbed_pane;

	/**
	 * Displays the on-line manual.
	 */
	private	HelpDialog		help_dialog;

	/**
	 * Displays ownership and version information.
	 */
	private	JMenuItem		about_menu_item;

	/**
	 * Makes the HelpDialog visible.
	 */
	private	JMenuItem		help_menu_item;


	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Sets up and displays the jProductionCritic GUI. Validate the default configurations file.
	 */
	private OuterFrame()
	{
		// Set border gaps
		int horizontal_gap = 4;
		int vertical_gap = 4;

		// Set the title of the window
		setTitle("jProductionCritic 1.2");	
		
		// Cause program to quit when the exit box is pressed
		addWindowListener(new WindowAdapter()
			{@Override public void windowClosing(WindowEvent e) {System.exit(0);}});

		// Set up the menus
		JMenuBar menu_bar = new JMenuBar();
		JMenu information_menu = new JMenu("Information");
		information_menu.setMnemonic('i');
		about_menu_item = new JMenuItem("About");
		about_menu_item.setMnemonic('a');
		about_menu_item.addActionListener(OuterFrame.this);
		information_menu.add(about_menu_item);
		help_menu_item = new JMenuItem("Help");
		help_menu_item.setMnemonic('h');
		help_menu_item.addActionListener(OuterFrame.this);
		information_menu.add(help_menu_item);
		menu_bar.add(information_menu);
		setJMenuBar(menu_bar);

		// Set up help dialog box
		help_dialog = new HelpDialog( "ProgramFiles" + File.separator + "Manual" + File.separator + "contents.html",
									  "ProgramFiles" + File.separator + "Manual" + File.separator + "splash.html" );

		// Set up the tabs
		tabbed_pane = new JTabbedPane();
		analysis_panel = new AnalysisPanel(this);
		tabbed_pane.addTab("Analysis", analysis_panel);
		settings_panel = new SettingsPanel(this, analysis_panel, "./jProductionCriticConfigs.jpc");
		tabbed_pane.addTab("Configuration Settings", settings_panel);
		
		// Initialize layout settings
		Container content_pane = getContentPane();
		content_pane.setLayout(new BorderLayout(horizontal_gap, vertical_gap));

		// Add the tabs to the GUI
		content_pane.add(tabbed_pane, BorderLayout.CENTER);

		// Position at the left corner of the screen with a size of 1024 x 768
		setBounds (0, 0, 1024, 768);

		// Display the GUI
		this.setVisible(true);
		
		// Validate the default configurations file
		settings_panel.parseAndDisplayConfigurationSettings();

		// Set the default selected pane to the analysis_panel
		tabbed_pane.setSelectedIndex(0);
	}

	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * Run the private constructor of this class, thus setting up and displaying the jProductionCritic GUI.
	 * 
	 * @return	A newly instantiated OuterFrame object.
	 */
    public static OuterFrame startGUI()
	{
		return new OuterFrame();
	}
	
	/**
	 * Return the path contained in the Current Configuration Settings File text field of the Settings Panel.
	 * 
	 * @return	The file path of the configurations file to parse.
	 */
	public String getConfigFilePath()
	{
		return settings_panel.getConfigFilePath();
	}
	
	/**
	 * Save the contents of the configurations settings table to the path specified in the Current 
	 * Configuration Settings File text field of the Settings Panel. Note that all possible
	 * settings are first initialized to default values, and only then are the values in the table added 
	 * (possibly overwriting default values). An error dialog box is displayed if any settings include
	 * invalid values, and processing is canceled. Settings revert to defaults if there is a problem with
	 * writing the file itself.
	 * 
	 * @return	Returns whether or not processing and saving occurred.
	 */
	public boolean saveConfigurationSettings()
	{
		return settings_panel.saveEnteredConfigSettings(getConfigFilePath());
	}
	
	/**
	 * Sets the focus on the AnalysisPanel in the JTabbedPane.
	 */
	public void setFocusToAnalysisPanel()
	{
		tabbed_pane.setSelectedIndex(0);
	}

	/**
	 * Sets the focus on the SettingsPanel in the JTabbedPane.
	 */
	public void setFocusToSettingsPanel()
	{
		tabbed_pane.setSelectedIndex(1);
	}

	/**
	* Perform appropriate actions in response to GUI interactions.
	*
	* @param	event	The event that is to be reacted to.
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		// React to the about_menu_item
		if (event.getSource().equals(about_menu_item))
		{
			AboutDialog about = new AboutDialog( this,
												 "jProductionCritic 1.2.2",
												 "Cory McKay",
												 "2017 (GNU GPL)",
												 "Marianopolis College and CIRMMT" );
		}

		// React to the view_manual_menu_item
		if (event.getSource().equals(help_menu_item))
			help_dialog.setVisible(true);
	}
}