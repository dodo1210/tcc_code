/*
 * HelpDialog.java
 * Version 3.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.gui.templates;

import javax.swing.*;

/**
 * Dialog box that allows users to read help (or other) documents in HTML
 * format. Has two frames. The left one displays a list of contents and the
 * right one displays the current help page. Any links clicked on in either
 * frame will be displayed in the right frame.
 *
 * @author Cory McKay
 */
public class HelpDialog
     extends JDialog
{
     /* FIELDS ****************************************************************/
     
     
     /**
      * Where the actual help data is displayed.
      */
     private HTMLFramesPanel display_panel;
     
     
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Set up the dialog box and display the default help file.
      *
      * @param	contents_page_path	The path to the HTML page containing the
      *                                 list of help topics.
      * @param	default_page_path	The path to the HTML page containing the
      *                                 default help page.
      */
     public HelpDialog(String contents_page_path, String default_page_path)
     {
          // Give the dialog box its owner, its size, its title and make it non-modal
          super();
          setTitle("Help");
          setModal(false);
          setSize(1004, 748);
          
          // Add the display panel
          display_panel = new HTMLFramesPanel(contents_page_path, default_page_path);
          add(display_panel);
     }
}
