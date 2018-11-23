==========================================================================
 jProductionCritic 1.2.2
 by Cory McKay
 Marianopolis College and CIRMMT
 Copyright (C) 2017 (GNU GPL)
==========================================================================


-- OVERVIEW -- 

jProductionCritic is an open-source educational framework for automatically detecting
technical recording, editing and mixing problems in audio files. It is intended to be
used as a learning and proofreading tool by students and amateur producers, and can 
also assist teachers as a timesaving tool when grading recordings.

A number of novel error detection algorithms are implemented by jProductionCritic. 
Problems detected include edit errors, clipping, noise infiltration, poor use of 
dynamics, poor track balancing, and many others. The error detection algorithms are 
highly configurable, in order to meet the varying aesthetics of different musical 
genres (e.g. Baroque vs. noise music). Effective general-purpose default settings were
developed based on experiments with a variety of student pieces, and these settings were
then validated using a reserved set of student pieces. The settings for each error 
checking algorithm may be accessed and modified via the jProductionCritic configuration
file. Multiple such configuration files may be saved and applied to different kinds of
music if desired.

jProductionCritic is also designed to serve as an extensible framework to which new error detection modules can be easily plugged in. It is hoped that this will help to galvanize
MIR research relating to audio production, an area that is currently underrepresented in
the MIR literature, and that this work will also help to address the current general lack
of educational production software.

jProductionCritic is part of the jMIR project, and as such can be easily integrated with
other jMIR applications. However, it is also designed to be used entirely independently
if desired. Like the rest of jMIR, jProductionCritic is open-source and available for 
free. It is implemented in Java in order to maximize cross-platform utilization.

jProductionCritic may be used via either a command line interface or a GUI. The software
also has a well-documented API in order to facilitate the use of jProductionCritic as a 
library incorporated into other software. The results of error processing may be viewed
directly with the jProductionCritic, or may be saved in a variety of formats.


-- MANUAL --

A file entitled "Manual.html" is extracted upon installation. This links to 
HTML files that provide many more details than this README document.


-- GETTING MORE INFORMATION --

More information on jMIR is available at http://jmir.sourceforge.net.

Please contact Gabriel Vigliensoni (gabriel@music.mcgill.ca) or Cory
McKay (cory.mckay@mail.mcgill.ca) with any bug reports or questions
relating to the software. 


-- LICENSING AND LIABILITY --

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You may obtain a copy of the GNU General Public License by writing to the Free
Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.


-- COMPATIBILITY --

This software is written in Java, which means that it can theoretically be run
on any system that has the Java Runtime Environment (JRE) installed on it. It 
is recommended that this software be used with Windows, however, as it was 
developed and tested under the Windows operating system, and the interface's 
appearance was developed under Windows. Although the software should 
theoretically run under OS X, Linux, Solaris or any other operating system with
the JRE installed on it, users should be advised that the software has not yet 
been fully tested on other platforms, so difficulties may be encountered.

This software was developed with version 7 of Java, so it is suggested that the 
corresponding version or higher of the JRE be installed on the user's computer.


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

- jProductionCritic: The jProductionCritic project, presented in the form of
a NetBeans project. Contains the following directories and files:
	- dist: Contains a pre-compiled jWebMjProductionCriticiner2.jar file
	(and associated libraries) for direct inclusion in other projects.
	- javadoc: Javadoc documentation for the project source code.
	- lib: Code library settings used by the NetBeans IDE.
	- Licenses: Licenses associated with the project and its dependencies.
	- nbproject: NetBeans project files. This is only relevant to those
	wishing to use the software in a NetBeans IDE context; it is certainly
	possible to use the software in other development environments as well.
	- ProgramFiles: Contains files used by the software.
	- src: The project's source code.
	- build.xml: NetBeans build instructions. Only relevant if using the
	NetBeans IDE.
	- jProductionCriticConfigs.jpc: A configuration settings file.
	- manifest.mf: The manifest used when building the project Jar file.
	- Manual.html: The parent HTML page for the manual.
	- README.txt: Basic overall documentation of the project.
- Third-Party-Jars: Contains the distributable third-party software used by 
the project. Also includes the jar files for other jMIR projects that this
project has as adependencies, if any. These need to be included in the project's
build (in the NetBeans context, this means adding the jar files found here as 
libraries).

Both the user and developer versions of jProductionCritic make use of a configuration
settings file. A default configuration file called jProductionCriticConfigs.jpc is
included with both the user and developer distributions, which the user may edit or
replace. A new configuration with default settings is automatically generated by 
jProductionCritic if this file is deleted.


-- RUNNING THE SOFTWARE -- 

A file named "jProductionCritic.jar" is produced upon installation. jProductionCritic 
is used by running this file using command line arguments specifying what kind of
processing is to be performed. If the user wishes to use jProductionCritic via its
GUI, then jProductionCritic should be run with no flags (see below). Alternatively, 
the user may simply double click on the jar, although this may result in memory 
problems during processing (see below).

If the user wishes to use jProductionCritic directly from the command line, then 
command line arguments consisting of flag/value pairs must be used, where the flag 
comes first and is preceded with a "-". Each flag must be followed by its associated 
value. The one exception to this is that a lone flag of "-help" will print a list of
valid flags with explanations of what they are. Invalid command line arguments will
also result in the printing out of a list of valid flags with explanations of what
they are. The user may choose to omit certain flag/value pairs if desired, as 
specified below. The ordering of the flags is irrelevant.

Whether the user uses the GUI or command line interface, jProductionCritic's operation
is  governed by the preferences contained in its configuration settings file. See the
manual for details on these configuration settings.

The following is a list of the flags that may be used to choose which audio files are 
to be checked for technical production errors by jProductionCritic:

* -check: The path of an audio file to test for technical production or recording errors.
* -batchcheck: The path of a directory. All audio files in this directory (but NOT
its sub-directories) will be tested for technical production or recording errors.

One of the above flags must be present, but not both.

jProductionCritic can process the following kinds of audio files: mp3, wav, aiff, aifc,
au and snd. Any files that cannot be parsed by jProductionCritic will cause an error
message to be generated.

The following is a list of the flags that may be used to choose which kinds of reports
indicating technical production errors are to be generated by jProductionCritic:

* -reportcmdline: Prints the results of jProductionCritic analysis of audio files to
standard out if this flag is followed by a value of "yes".
* -reporttxt: The path at which to save one or more text files containing the results
of jProductionCritic analysis of audio files (one analysis file is generated for each
audio file analyzed). If a single audio file is analyzed via the -check option, then
this flag should specify the path of the output report file. If potentially multiple
files are to be analyzed via the -batchcheck option, then this flag should specify
the path of a directory in which the results files can be stored (with auto-generated
file names). Any files that already exist with the same paths as the generated reports
will automatically be overwritten.
* -reporthtml: The path at which to save one or more HTML files containing the results
of jProductionCritic analysis of audio files (one analysis file is generated for each
audio file analyzed). If a single audio file is analyzed via the -check option, then
this flag should specify the path of the output report file. If potentially multiple
files are to be analyzed via the -batchcheck option, then this flag should specify the
path of a directory in which the results files can be stored (with auto-generated file
names), as well as an index page. Any files that already exist with the same paths as 
the generated reports will automatically be overwritten.
* -reportaudacity: The path at which to save one or more Audacity label track files
containing the results of jProductionCritic analysis of audio files (one Audacity label
track file is generated for each audio file analyzed). If a single audio file is analyzed
via the -check option, then this flag should specify the path of the output label track
file. If potentially multiple files are to be analyzed via the -batchcheck option, then
this flag should specify the path of a directory in which the output files can be stored
(with auto-generated file names). Any files that already exist with the same paths as
the generated reports will automatically be overwritten. Note that automatically 
generated Audacity label track file names are given the .txt extension, which could cause
them to overwrite .txt report files set to be generated in the same directory.
* -reportacexml: The path at which to save a single ACE XML file containing the results 
of jProductionCritic analysis of one or more audio files. If a file already exists at this
path, then it will automatically be overwritten. Note that, unlike the other report types,
only one file is generated, regardless of whether the -check or -batchcheck extraction 
option is used. Also, the path specified here must always refer to a file path, not a
directory, and no directory in the path will be created if it does not already exist.
* -reportwekaarff: The path at which to save one or more Weka ARFF files containing the 
results of jProductionCritic analysis of audio files (one ARFF file is generated for each 
audio file analyzed). If a single audio file is analyzed via the -check option, then this 
flag should specify the path of the output ARFF file. If potentially multiple files are
to be analyzed via the -batchcheck option, then this flag should specify the path of a
directory in which the output files can be stored (with auto-generated file names). Any 
files that already exist with the same paths as the generated reports will automatically
be overwritten.

At least one of the above report options must be present and selected for processing to 
occur, but no more than one is necessary.

More information on these reports is available in the section of the manual on reports.

The details of jProductionCritic's error analysis processing are specified in its 
configuration settings file. The following optional flag allows the user to specify which
configuration file to use:

* -configfile: The path of a configuration file containing error checking settings. 
jProductionCritic will revert to the default file path if this setting is not specified.
A default configuration file is auto-generated if no configuration file can be found.

Examples are provided below showing sample ways in which jProductionCritic can be run from
the command line. Note that, as is standard practice, flag values containing spaces are
enclosed in quotation marks. Note also that the "-mx1000M" Java Virtual Machine option is
also used in these examples to reserve 1000 MB of memory for the virtual machine to operate. 
Although this is not strictly necessary for jProductionCritic processing, it is a good idea
in general to ensure that the VM does not run out of memory.

Example 1) Run the jProductionCritic GUI:

java -mx1000M -jar jProductionCritic.jar

Example 2) Check a single recording called "test.wav" in the current directory for errors
and report the results to standard out using the default configuration settings:

java -mx1000M -jar jProductionCritic.jar -check ./test.wav -reportcmdline yes

Example 3) Check a single recording called "test.wav" in the current directory for errors
and report the results to standard out using the configurations settings specified in a
file called "myconfigs.jpc":

java -mx1000M -jar jProductionCritic.jar -check ./test.wav -reportcmdline yes -configfile 
myconfigs.jpc

Example 4) Check a single recording called "test.wav" in the current directory for errors. 
Generate an Audacity label track specifying any errors detected during analysis in the
current directory, and also report the results to standard out:

java -mx1000M -jar jProductionCritic.jar -check ./test.wav -reportaudacity ./test_ALT.txt 
-reportcmdline yes

Example 5) Check all compatible audio files in the directory "Assignment 1" for errors.
Generate a set of text file reports in the directory "Reports":

java -mx1000M -jar jProductionCritic.jar -batchcheck "./Assignment 1" -reporttxt ./Reports


-- CAVEAT -- 

Different styles of music can have dramatically different signal characteristics. For example,
a live metal recording will have far more spectral noise than a classical solo flute studio 
recording. The error detection thresholds therefore need to be much more forgiving in the
former case (in order to avoid false positives) but more stringent in the latter case (in 
order to avoid false negatives).

The default configuration settings distributed with jProductionCritic are designed to be 
applicable to as wide a variety of recording scenarios and musical styles as possible. Having 
noted this, one will inevitably achieve better results by fine-tuning the settings to one's 
particular needs. Users are therefore strongly encouraged to experiment with the 
configuration settings to meet their own particular needs. It may be particularly useful 
to develop a number of settings profiles to use for different recording scenarios.


-- UPDATE HISTORY -- 

Changes introduced in Version 1.2.2:

* Updated support libraries
* Minor documentation updates

Changes introduced in Version 1.2:

* Additional fine-tuning of default configuration settings
* Minor bug fixes

Changes introduced in Version 1.1 :

* Addition of HTML Reports
* Implementation of the GUI
* Minor bug fixes