==========================================================================
 jMIRUtilities 1.4.5
 by Cory McKay - CIRMMT / Marianopolis College / McGill University
 Copyright (C) 2017 (GNU GPL)
==========================================================================


-- OVERVIEW -- 

jMIRUtilities is an open source set of tools for performing miscellaneous
tasks relating to the jMIR music classification research software suite.
More information on jMIR is available at http://jmir.sourceforge.net. The
current release version can perform the following tasks:

- Batch audio song identification: Audio fingerprinting is used to identify 
all of the MP3 files in a specified directory and its sub-directories. A 
tab-delimited text file is output to a specified location, with one line per
audio file, and where the file path, song title and artist name are specified
for each file. Since this is intended for use in experimental evaluations,
any embedded metadata tags are ignored, and only the audio signal itself is
used to identify tracks. jMIRUtilities uses the jSongMiner API to perform the
Echo Nest powered identifications.
- Manual instance labelling: A simple GUI is provided for quickly batch
labelling sets of audio, MIDI or other files and generating a corresponding
ACE XML Instance Label file.
- Automatic instance labelling: An ACE XML Instance Label file may be 
automatically generated based on a tab-delimited text file containing
labelled instances. This is convenient for model classifications that
are stored in a spreadsheet table, for example, which may easily be
exported to such a tab-delimited text files.
- Extract labels from iTunes files: An iTunes XML file may be parsed
and a delimited text file generated that lists the File Path, Recording
Title, Artist Name, Composer Name, Album Title and/or Genre Names for
each recording in the file, as requested by the user. This can be a useful
pre-processing step for generating ACE XML Instance Label files from
iTunes XML files via jMIRUtilities’ automatic instance labelling
functionality.
- Modify instance identifiers: Text files containing mappings between old
instance identifiers and new instance identifiers may be used to 
automatically update the instance identifiers found in an ACE XML Feature 
Values file.
- Feature merging: ACE XML Feature Value files that contain different
features for the same instances may be merged into a single ACE XML Feature
Value file. ACE XML Feature Description files that correspond to these
Feature Value files may also be merged. This might be used, for example, to
combine feature values extracted by jAudio with feature values extracted by
jWebMiner for the same pieces of music.
- Split instances by feature types: An ACE XML Feature Values file can be
automatically broken into one new ACE XML Features Values file for each
feature that is contained in the original file. Each new file only contains
feature values for its associated feature, and only contains instances that
have the given feature value available.
- Convert ACE XML feature value files to either Weka ARFF or CSV files.


-- GETTING MORE INFORMATION --

More information on jMIR is available at http://jmir.sourceforge.net.

Please contact Cory McKay (cory.mckay@mail.mcgill.ca) with any bug reports
or questionsrelating to the software. 


-- LICENSING AND LIABILITY --

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You may obtain a copy of the GNU General Public License by writing to the Free
Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

This project also includes software developed by the Apache Software 
Foundation (http://www.apache.org), namely the Xerces library, which is used
to parse XML files. The Xerces license can be accessed via the manual.

This project also makes use of the Echo Nest API. Licensing information is
included in the installaction package.


-- COMPATIBILITY --

This software is written in Java, which means that it can theoretically be run
on any system that has the Java Runtime Environment (JRE) installed on it. It 
is recommended that this software be used with Windows, however, as it was 
developed and tested under the Windows operating system, and the interface's 
appearance was developed under Windows. Although the software should 
theoretically run under OS X, Linux, Solaris or any other operating system with
the JRE installed on it, users should be advised that the software has not yet 
been fully tested on other platforms, so difficulties may be encountered.

This software was developed with version 1.6.0_16 of the JDK (Java Development 
Kit), so it is suggested that the corresponding version or higher of the JRE be
installed on the user's computer.


-- INSTALLING THE JAVA RUNTIME ENVIRONMENT --

If your system already has the JRE installed, as will most typically be the
case, you may skip this section. If not, you will need to install the JRE in
order to run this project. The JRE can be downloaded for free from the Java web
site. The JDK typically includes the JRE, or the JRE can simply be installed
alone.

When the JRE download is complete, follow the installation instructions that
come with it in order to install it.


-- INSTALLING THE PROJECT --

The project can be accessed at http://jmir.sourceforge.net. This distribution
includes a pre-compiled Jar file, the source code and extensive documentation. 
All of this is delivered in a zipped file, from which the project files can be
extracted.

There are two versions of the project, namely the developer version and the
user version. The user version contains everything needed to run the project,
but does not include any source code. The developer version does include
source code, presented in the form of a NetBeans project.

The user version unzips into a single directory. Installation simply involves 
copying this directory into any desired location.

The following directories are contained in the developer zip file:

- jMIRUtilities: The jMIRUtilities project, presented in the form of a NetBeans
project. Contains the following directories and files:
	- dist: Contains a pre-compiled jMIRUtilities.jar file (and associated
	libraries) for direct inclusion in other projects.
	- javadoc: Javadoc documentation for the project source code.
	- Licenses: Licenses associated with the project and its dependencies.
	- nbproject: NetBeans project files. This is only relevant to those
	wishing to use the software in a NetBeans IDE context; it is certainly
	possible to use the software in other development environments as well.
	- src: The project's source code.
	- build.xml: NetBeans build instructions. Only relevant if using the
	NetBeans IDE.
	- manifest.mf: The manifest used when building the project Jar file.
	- README.txt: Basic overall documentation of the project.
- Third_Party_Jars: Contains the distributable third-party software used by 
the project. Also includes the jar files for other jMIR projects that this
project has as adependencies, if any. These need to be included in the project's
build (in the NetBeans context, this means adding the jar files found here as 
libraries).


-- RUNNING THE SOFTWARE -- 

A file named "jMIRUtilities.jar" is produced upon installation.
The software is accessed via the command line (e.g. the DOS
prompt). To access DOS under Windows, for example, go to the Start 
Menu, select Run and type "cmd". Then use the "cd" command to move to
the directory that contains the jMIRUtilities.jar file. In that directory, 
type:

	java -jar jMIRUtilities.jar -help

This will provide the user with the valid command line flags that can
be used with jMIRUtilities.

It should be noted that the JRE does not always allocate sufficient
memory for jMIRUtilities to process large music collections. Running 
jMIRUtilities using the above method could therefore result in an out
of memory error (although this is relatively rare).

It is therefore sometimes preferable to manually allocate a greater
amount of memory to jMIRUtilities before running it. 250 MB should be more
than enough for most situations. This can be done by entering the 
following at the command prompt:

	java -ms16M -mx250M -jar jMIRUtilities.jar


-- PARTIAL LIST OF UPDATES SINCE VERSION 1.0 -- 

jMIRUtilities 1.4.5:
- Added -convertFeatureValuesToCSV functionality for
converting ACE XML Feature Value files to CSV files containing
both feature values and instance identifiers
- Updated support libraries
- Minor documentation updates

jMIRUtilities 1.4.4:
- Added -convertFeatureValuesToARFF functionality for
converting ACE XML Feature Value files to ACE XML

jMIRUtilities 1.4.3:
- Added -mergeInstances functionality for merging instances
stored in separate ACE XML files

jMIRUtilities 1.4.2:
- Added batch audio song identification functionality

jMIRUtilities 1.4.1:
- Fixed a bug related to parsing ACE XML and iTunes XML files.
- Includes updated support libraries (ACE, jMusicMetaManager
 and UtilityClasses).

jMIRUtilities 1.4:
- New processing functionality
- Includes updated support libraries (ACE and UtilityClasses)