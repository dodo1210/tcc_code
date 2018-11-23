/*
 * ProcessCoordinator.java
 * Version 3.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.gui.progressbars;


/**
 * An interface for the class that performs the actual processing that is
 * measured by a progress bar.
 *
 * @author Cory McKay
 */
public interface ProcessCoordinator
{
     /**
      * Set the coordinator for the progress bar itself.
      *
      * @param task_coordinator    The task coordinator.
      */
     public void setDoubleProgressBarTaskCoordinator(DoubleProgressBarTaskCoordinator task_coordinator);
     
     
     /**
      * Find the total number of top-level that need to be performed.
      *
      * @return     The number of top-level tasks.
      */
     public int findNumberOfTopLevelTasks();
     
     
     /**
      * Perform actual processing.
      *
      * @throws     Exception An informative report of any problem that occurs.
      */
     public void performProcessing() throws Exception;
}