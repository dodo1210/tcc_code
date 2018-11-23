#!/bin/bash
###########################################
#
# If you can't run this script from GUI, 
# run a terminal, then change directory 
# (cd) to this script's and type in
#
#     ./Install.sh
#
# We are confident that the managers of this
# Linux distribution are sorry for the 
# inconvenience...
#
#
############################################

###################################################
#		Functions
###################################################

# Get script's own path
function getpath
{
pushd `dirname $0` > /dev/null
echo `pwd -P`
popd > /dev/null
}



black='\033[0;30m'
blue='\033[0;34m'
green='\033[0;32m'
cyan='\033[0;36m'
red='\033[0;31m'
purple='\033[0;35m'
orange='\033[0;33m'
ltgray='\033[0;37m'

dkgray='\033[1;30m'
ltblue='\033[1;34m'
ltgreen='\033[1;32m'
ltcyan='\033[1;36m'
ltred='\033[1;31m'
ltpurple='\033[1;35m'
yellow='\033[1;33m'
white='\033[1;37m'


NC='\033[0m'

function echotrans
{
if [ "$1" = "fr" ] || [ "$1" = "FR" ] || [ "$1" = "Fr" ]; then
	echo -e "$3"
else
	echo -e "$2"
fi
}

function getanykey
{
if [ "$1" = "fr" ] || [ "$1" = "FR" ] || [ "$1" = "Fr" ]; then
	read -n1 -r -p "$3" key
else
	read -n1 -r -p "$2" key
fi
}


# Translated echo : lang, index
function myecho
{
echlang=$1
set $2
case "$1" in
	dskicon)
	echotrans $echlang "${purple}Installing desktop icon...${NC}" "${purple}Installation de l'icône de bureau...${NC}"
	;;
	demo)
	echotrans $echlang "${purple}Copying demo files...${NC}" "${purple}Copie des fichiers de démo...${NC}"
	;;
	done)
	echotrans $echlang "${purple}Done.${NC}" "${purple}Terminé.${NC}"
	;;
	error)
	echotrans $echlang "${ltred}Error. Operation not completed.${NC}" "${ltred}Erreur. Opération non effectuée.${NC}"
	;;
	1st)
	echotrans $echlang "${purple}Running application for the first time...${NC}" "${purple}Premier lancement de l'application...${NC}"
	;;
	complete)
	echotrans $echlang "Install complete." "Installation terminée."
	;;
	~anykey)
	getanykey $echlang "Press any key to close..." "Appuyez une touche pour fermer..."
	;;
	*)
	echo "??? $1 in $echlang"
	;;
esac
}


# system language
function getlang
{
echo $LANG | cut -d'_' -f 1
}

# Call to "which"
function dowhich
{
which "$1" 2>/dev/null | grep -v "no $1"
}

switch=""
if [ ! -z $1 ]; then
	if [ $1 = "-v" ] || [ $1 = "-V" ]; then
		switch="-v "
		shift
	fi
fi

scriptpath=$(getpath)
# Program-specific variables
source "$scriptpath/settings.sh"
 
prgname=$a
dskname=$b
pngname=$c
mimename=$d
fontfoldername=$e

lang=$(getlang)

"$scriptpath/scripts/rootexec.sh" "\"$scriptpath/scripts/process.sh\" $switch\"$prgname\" \"$dskname\" \"$pngname\" \"$mimename\" \"$fontfoldername\""
myecho $lang "demo"
docdir=$(xdg-user-dir DOCUMENTS)
if [ -z "$docdir" ]; then
	# Find home dir
	docdir=$HOME
	if [ -z "$docdir" ] || [ ! -d "$docdir"]; then
		docdir="/home/$USER"
		if [ -z "$docdir" ] || [ ! -d "$docdir"]; then
			docdir="/home"
		fi
	fi
	if [ -d "$docdir/Documents" ]; then
		docdir="$docdir/Documents"
	fi
fi
docdir="$docdir/Myriad Documents/PDFtoMusicDemos"
mkdir -p -m 777 "$docdir" &>/dev/null
cp -r "$scriptpath/InstallFiles/PDFtoMusicDemos/"* "$docdir" &>/dev/null
myecho $lang "done"
myecho $lang "dskicon"
xdg-desktop-icon install --novendor "/usr/share/applications/$dskname" &>/dev/null
myecho $lang "done"


if [ $# -eq 0 ] || [ $1 = "0" ]; then
	echo
	myecho $lang "1st"
	nohup "/usr/bin/$prgname" &>/dev/null &
	sleep 2s
fi
myecho $lang "complete"
myecho $lang "~anykey"

