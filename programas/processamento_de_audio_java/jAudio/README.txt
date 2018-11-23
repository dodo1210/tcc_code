==========================================================================
 jAudio 2.0.1 Pre-Release
 by Cory McKay and Daniel McEnnis
==========================================================================


-- OVERVIEW -- 

jAudio is an open source Java tool for extracting features from audio
files. It was originally designed by Cory McKay during the early development
stages of the jMIR music classification research software suite
(http://jmir.sourceforge.net), but is now developed independently by Daniel
McEnnis.

jAudio 2.0.1 is an experimental expansion branching off from jAudio 0.4.5. 
This version consists of a few modifications made by Cory McKay after many
others had been made by Daniel McEnnis. Daniel McEnnis has made many of his
own changes since then, and users are encouraged to use his much more updated
version available and described here:

https://sourceforge.net/projects/jaudio/
http://jaudio.sourceforge.net


-- SUMMARY OF CHANGES IN JAUDIO 2.0.1 -- 

- Minor documentation updates.
- Updates to unit testing libraries.


-- SUMMARY OF CHANGES IN JAUDIO 2.0 -- 

- jAudio has been ported into NetBeans project in order to facilitate
modifications. OCVolume has also been ported directly into the jAudio code.
- jAudio may now extract features from Ogg Vorbis audio files, in addition
to the formats previously supported.
- Project linking has been updated so that it is no longer necessary to place
any files in the JRE ext folder.
- An installer is no longer needed or provided.


-- KNOWN ISSUES --

- It is necessary to manually set the <pluginFolder> field of the 
features.xml file to point to the folder holding new features. No change
is necessary if only the default features are being used.
- The on-line help in the GUI has been temporarily disabled. 


-- RUNNING THE SOFTWARE -- 

The easiest way to use jAudio is through its GUI. This can be done by typing
the following at the command line:

java -mx500M -jar jAudio.jar

jAudio can also be run from the command line using batch files, which are XML
files that may be either created manually or saved using the jAudio GUI. This
can be done by running jAudio at the command line as follows (thereybe accessing
a batch file called, in this case, batch.xml):

java –mx500M -jar jAudio.jar -b batch.xml 


-- LICENSING -- 

This program is free software; you can redistribute it
and/or modify it under the terms of the GNU General 
Public License as published by the Free Software Foundation.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public 
License along with this program; if not, write to the Free 
Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139,
USA.

A copy of the GNU General Public License is available via
the HTML manual.

The jAudio software makes use of third-party products,
including software developed by the Apache Software Foundation
(http://www.apache.org/). This is the Xerces library, which is
used to parse XML files.