==========================================================================
 jWebMiner 2.0.2
 by Cory McKay (jWebMiner 1.x) and Gabriel Vigliensoni (jWebMiner 2.x)
 CIRMMT / Marianopolis College / McGill University
 Copyright (C) 2017 (GNU GPL)
==========================================================================


-- OVERVIEW -- 

jWebMiner is an open source software system for automatically extracting
cultural features from the web for use in classifying and/or measuring
similarity between text terms. It is designed with the particular needs
of music research in mind, but may easily be used in many other areas of
research.

At its most basic level, the software operates by using web services to
extract hit counts from search engines and to extract information related
to Last.FM social tags. Functionality is also available for calculating a
variety of statistical features based on search engine counts, for
variably weighting web sites or limiting searches only to particular sites,
for excluding hits that do or do not contain particular filter terms, for
defining synonym relationships between search strings, and for applying a 
number of additional search configurations. A particular emphasis has also
been placed on extensibility so that new functionality can be easily added.

jWebMiner can parse terms from delimited text files, from class names 
found in Weka ARFF or ACE XML files, or from a variety of different fields
of iTunes XML files. They can also be entered manually by the user into
the jWebMiner GUI.

Detailed results and configuration records can be saved in HTML reports.
Final feature values can also be exported to ACE XML, Weka ARFF or 
newline delimited text files.

jWebMiner was developed as part of the jMIR music classification research
software suite, and may be used either as part of this suite or
independently. 


-- IMPORTANT WARNINGS --

1) In the interim since jWebMiner was originallyl developed, Google, Yahoo!
and Last.FM have likely changed or deprectated the APIs that jWebMiner2 uses. 
This means that jWebMiner2 may no longer be able to access their web services
until it is updated to use their new APIs.

2) This manual has not yet been updated to fully reflect the improvements to
jWebMiner2. It particular, it does not yet include instructions for using 
jWebMiner2's Last.FM functionality.


--- MANUAL ---

A file entitled "Manual.html" is extracted upon installation. This links to 
HTML files that provide many more details than this README document.


--- GETTING MORE INFORMATION ---

More information on jMIR is available at http://jmir.sourceforge.net.

Please contact Gabriel Vigliensoni (gabriel@music.mcgill.ca) or Cory
McKay (cory.mckay@mail.mcgill.ca) with any bug reports or questions
relating to the software. 


--- LICENSING AND LIABILITY ---

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You may obtain a copy of the GNU General Public License by writing to the Free
Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

This project software makes use of severalincluded third-party products.

The University of Waikato Weka data mining package is used to parse and
save Weka ARFF files. This software is also available under the GNU GPL,
and more information on it is available at 
http://www.cs.waikato.ac.nz/ml/weka. 

This project also includes software developed by the Apache Software 
Foundation (http://www.apache.org), namely the Xerces library, which is used
to parse XML files. The Xerces license can be accessed via the manual.

The Yahoo! SDK is used to submit queries to the Yahoo! engine. This SDK 
comes with a BSD license, which may be accessed via the jWebMiner manual. 
Queries submitted to Yahoo's Web Services must comply with Yahoo!'s terms 
of service, available at http://docs.yahoo.com/info/terms. 

Similarly, a license key is needed to query the LastFM engine. You can test
jWebMiner with the sample license key for online testing provided, but
the user should provide another one (available at 
http://www.last.fm/api/account). Queries submitted to the LastFM's web
services must comply with their terms and conditions, available at
http://www.last.fm/api/tos.


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

- jWebMiner2: The jWebMiner2 project, presented in the form of a NetBeans project.
Contains the following directories and files:
	- dist: Contains a pre-compiled jWebMiner2.jar file (and associated
	libraries) for direct inclusion in other projects.
	- javadoc: Javadoc documentation for the project source code.
	- Licenses: Licenses associated with the project and its dependencies.
	- nbproject: NetBeans project files. This is only relevant to those
	wishing to use the software in a NetBeans IDE context; it is certainly
	possible to use the software in other development environments as well.
	- ProgramFiles: Contains files used by the software.
	- SampleFiles: Files that can be used to help learn to use the software.
	- src: The project's source code.
	- build.xml: NetBeans build instructions. Only relevant if using the
	NetBeans IDE.
	- Manual.html: The parent HTML page for the manual.
	- manifest.mf: The manifest used when building the project Jar file.
	- README.txt: Basic overall documentation of the project.
- Third-Party-Jars: Contains the distributable third-party software used by 
the project. Also includes the jar files for other jMIR projects that this
project has as adependencies, if any. These need to be included in the project's
build (in the NetBeans context, this means adding the jar files found here as 
libraries).


-- RUNNING THE SOFTWARE -- 

A file named "jWebMiner2.jar" is produced upon installation. The
simplest way to start jWebMiner is to simply double click on this 
file.

The software may also be accessed via the command line (e.g. the DOS
prompt). To access DOS under Windows, for example, go to the Start 
Menu, select Run and type "cmd". Then use the "cd" command to move to
the directory that contains the jWebMiner2.jar file. In that directory, 
type:

	java -jar jWebMiner2.jar

It should be noted that the JRE does not always allocate sufficient
memory for jWebMiner to process large music collections. Running 
jWebMiner using either of the above two methods could therefore result
in an out of memory error (although this is relatively rare).

It is therefore sometimes preferable to manually allocate a greater
amount of memory to jWebMiner before running it. 250 MB should be more
than enough for most situations. This can be done by entering the 
following at the command prompt:

	java -ms16M -mx250M -jar jWebMiner2.jar

Note that your computer must be connected to the internet for jWebMiner
to work. It may also be necessary to modify your firewall to allow it 
access.


-- UPDATES SINCE VERSION 1.0 -- 

jWebMiner 2.0.2:
- Updated support libraries.
- Minor documentation updates.

jWebMiner 2.0.1:
- Added functionality to enable web access via a proxy server
- Fixed minor inconsistency related to package names
- Fixed a bug related to parsing ACE XML and iTunes XML files.
- Updated UtilityClasses, ACE and jMusicMetaManager support libraries.

jWebMiner 2.0:
- Last.FM cross-tabulation feature extraction now supported, and results may
be combined with existing Yahoo! features
- Updated OS X compatibility (tested in version 10.5.)
- Google searches disabled due to Google's deprecation of SOAP web services
- Includes updated support libraries (ACE, UtilityClasses and Xerces 2.9.1).
