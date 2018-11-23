/**
 * ConfigurationSettingsTableModel.java
 * Version 1.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College
 */

package gui;

import javax.swing.table.DefaultTableModel;

/**
 * A TableModel used for displaying jProductionCritic configuration settings in a table. Provides a method to 
 * fill the table row by row after deleting everything in it. Also makes all configuration value cells
 * editable, but not their titles.
 * 
 * @author	Cory McKay
 */
public class ConfigurationSettingsTableModel
	extends DefaultTableModel
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Same constructor as DefaultTableModel. Constructs a ConfigurationSettingsTableModel with as many
	 * columns and rows as there are elements in column_names and and row_count. Each column's name is taken
	 * from the column_names array.
	 *
	 * @param	column_names	An array containing the names of the new columns. If this is null then the
	 *							model has no columns.
	 * @param	row_count		The number of rows the table holds.
	 */
	ConfigurationSettingsTableModel(Object[] column_names, int row_count)
	{
		super(column_names, row_count);
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * Deletes everything in the table and then fills it up one row at a time based on the given configuration
	 * settings.
	 *
	 * @param	configuration_settings	An array of configuration settings. The first index indicates the
	 *									particular setting. The first value of the second index indicates the
	 *									setting name, and the second value indicates that setting's value.
	 */
	public void fillTable(String[][] configuration_settings)
	{
		// Remove the contents of the table
		clearTable();

		// Populate each row one by one
		if (configuration_settings != null)
		{
			for (int i = 0; i < configuration_settings.length; i++)
			{
				// Set up row
				Object[] row_contents = new Object[2];
				row_contents[0] = configuration_settings[i][0];
				row_contents[1] = configuration_settings[i][1];

				// Add the row
				addRow(row_contents);
			}
		}
	}	
		
	/**
	 * Returns true for all cells except those in the first column., thereby indicating that which cells are
	 * editable.
	 *
	 * @param	row		The row whose value is to be queried.
	 * @param	column	The column whose value is to be queried.
	 * @return			Whether or not the given cell is editable
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (column == 0) return false;
		else return true;
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/


	/**
	 * Removes all contents of the table.
	 */
	private void clearTable()
	{
		while (getRowCount() != 0) removeRow(0);
	}
}