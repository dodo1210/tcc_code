#!/bin/bash
###########################################
#
# This install script has to be called as
# root, providing the following parameters
#
# => 	product name
#	desktop file name
#	icon name
#	mime type
#	font subfolder
#
############################################

# Verbose?
verbose=0
if [ ! -z "$1" ]; then
	if [ "$1" = "-v" ] || [ "$1" = "-V" ]; then
		verbose="1"
		shift
	fi
fi
prgname=$1
dskname=$2
icnname=$3
mimetype=$4
fontfolder=$5


#Fancy echo
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

###################################################
#		Functions
###################################################
function echotrans
{
if [ "$1" = "fr" ] || [ "$1" = "FR" ] || [ "$1" = "Fr" ]; then
	echo -e "$3"
else
	echo -e "$2"
fi
}

function getkeytrans
{
if [ "$1" = "fr" ] || [ "$1" = "FR" ] || [ "$1" = "Fr" ]; then
	read -p "$3" key
	case "$key" in
		O)
			key="y"
		;;
		o)
			key="y"
		;;
		T)
			key="A"
		;;
		t)
			key="a"
		;;
	esac
else
	read -p "$2" key
fi
}


# Translated echo : lang, index, params
function myecho
{
echlang="$1"
case "$2" in
	abort)
	echotrans $echlang "${ltred}Aborted.${NC}" "${ltred}Abandonné.${NC}"
	;;
	done)
	echotrans $echlang "${purple}Done.${NC}" "${purple}Terminé.${NC}"
	;;
	error)
	echotrans $echlang "${ltred}Error. Operation not completed.${NC}" "${ltred}Erreur. Opération non effectuée.${NC}"
	;;
	installTitle)
	echotrans $echlang "             ${ltblue}$3 Install" "         ${ltblue}Installation de $3"
	;;
	unkLib)
	echotrans $echlang "${ltred}Unknow package manager $3${NC}" "${ltred}Gestionnaire de paquets inconnu $3${NC}"
	;;
	libNotInstalled)
	echotrans $echlang "${orange}32-bits library ${NC}$3${orange} is not installed.${NC}" "${orange}La bibliothèque 32-bits ${NC}$3${orange} n'est pas installée.${NC}"
	;;
	installing)
	echotrans $echlang "${orange}Installing ${NC}$3${orange} through ${NC}$4" "${orange}Installation de ${NC}$3${orange} par ${NC}$4"
	;;
	Architecture)
	echotrans $echlang "${orange}Installing i386 architecture" "${orange}Installation de l'architecture i386"
	;;
	patient)
	echotrans $echlang "${orange}Please be patient, it may take several minutes to complete...${NC}" "${orange}Veuillez patienter, cette opération peut prendre plusieurs minutes...${NC}"
	;;
	skip)
	echotrans $echlang "${red}Skipping install of $3.${NC}" "${red}Installation de $3 non effectuée.${NC}"
	;;
	copy)
	echotrans $echlang "${purple}Copying files...${NC}" "${purple}Copie des fichiers...${NC}"
	;;
	regfont)
	echotrans $echlang "${purple}Registering fonts...${NC}" "${purple}Enregistrement des polices...${NC}"
	;;
	regfile)
	echotrans $echlang "${purple}Registering file type...${NC}" "${purple}Enregistrement des types de fichiers...${NC}"
	;;
	regmime)
	echotrans $echlang "${purple}Registering mime type...${NC}" "${purple}Enregistrement des types mime...${NC}"
	;;
	shortcut)
	echotrans $echlang "${purple}Installing desktop menu shortcut...${NC}" "${purple}Installation du raccourci de menu...${NC}"
	;;
	p2mp)
	echotrans $echlang "${purple}Creating p2mp command.${NC}" "${purple}Création de la commande p2mp.${NC}"
	;;
	depend)
	echotrans $echlang "${purple}Resolving dependencies...${NC}" "${purple}Résolution des dépendances...${NC}"
	;;
	pclinuxos1)
	echotrans $echlang "${orange}In order to install the application under ${NC}PCLinuxOS 64bits${orange}, a 32-bits repository${NC}" "${orange}Pour installer l'application sous ${NC}PCLinuxOS 64bits${orange}, un dépôt 32 bits${NC}"
	;;
	pclinuxos2)
	echotrans $echlang "${orange}has to be added.${NC}" "${orange}doit être ajouté.${NC}"
	;;
	~proceed)
	getkeytrans $echlang "Do you want to proceed? (<Y>es, <N>o)	 " "${purple}Voulez-vous le faire ? (<O>ui, <N>on)	${NC}"
	;;
	~install)
	getkeytrans $echlang "Do you want to install it now? (<Y>es, <N>o, <A>ll)	 " "Voulez-vous l'installer maintenant ? (<O>ui, <N>on, <T>ous)	 "
	;;
	*)
	echo "??? $1 in $echlang"
	;;
esac
}

# Get script's own path
function getpath
{
pushd `dirname $0` > /dev/null
echo `pwd -P`
popd > /dev/null
}

# Call to "which"
function dowhich
{
which "$1" 2>/dev/null | grep -v "no $1"
}

# system language
function getlang
{
echo $LANG | cut -d'_' -f 1
}

# distrib name? (incomplete)
function getdistro
{
if [ ! -f "/etc/lsb-release" ]; then
	echo "Other"
	return
fi
`grep -q Ubuntu /etc/lsb-release`
if [ $? -eq 0 ]; then	
	echo "Ubuntu" 
	return
fi
`grep -q PCLinuxOS /etc/lsb-release`
if [ $? -eq 0 ]; then	
	echo "PCLinuxOS" 
	return
fi
echo "Other"
}
# 32 or 64 bits system?
function getbits
{
case `uname -m` in
	i[3456789]86|x86|i86pc)
	echo "32"
	;;
	*)
	echo "64"
	;;
esac
}

#library installer program name
function getlibinst
{
local bcl
# gnome-terminal uses -e differently to other emulators
for bcl in "apt-get" "yum" "zypper" "urpmi" "pacman"; do
	if [ ! -z $(dowhich $bcl) ]; then 
		echo $bcl
		return
	fi
done
echo ""
}

#check if library installed
# => lib name, bits
function isinstalled
{
result=$(ldconfig -p | grep -w $1)
libits=$(getbits)

if [ -z "$result" ]; then
	echo ""
	return
fi

# 32 bits
if [ $libits = "32" ]; then
	echo "1"
	return
fi

#64 bits
nn=0
while read ldline ; do
	((nn++))
	if [[ ! $nn == 1 ]] && [[ ! $ldline =~ lib64 ]] && [[ ! $ldline =~ [xX]86[-_]64 ]]; then
		echo "1"
		return
	fi 
done <<< "$result"
echo ""
}


#Test installed lib and installs it
# syntax: checkinstlib <lang> <bits> <installer> <libname> <apt-get pkg> <yum pkg> <zypper pkg> <urpmi pkg> <pacman pkg>
function checkinstlib
{
if [ "$3" = "apt-get" ]; then
	lname=$5
	if [ $2 = "32" ]; then
		cmd="$3 -y install $lname"
	else
		cmd="$3 -y install $lname:i386"
	fi
elif [ "$3" = "yum" ]; then
	lname=$6
	if [ $2 = "32" ]; then
		cmd="$3 -y install $lname"
	else
		cmd="$3 -y install $lname.i686"
	fi
elif [ "$3" = "zypper" ]; then
	lname=$7
	if [ $2 = "32" ]; then
		cmd="$3 --non-interactive install $lname"
	else
		cmd="$3 --non-interactive install $lname-32bit"
	fi
elif [ "$3" = "urpmi" ]; then
	lname=$8
	cmd="$3 --force $lname"
elif [ "$3" = "pacman" ]; then
	lname=$9
	if [ $2 = "32" ]; then
		cmd="$3 -Syu --noconfirm $lname"
	else
		cmd="$3 -Syu --noconfirm lib32-$lname"
	fi
else
	myecho $lang "unkLib" "$3"
	myecho $lang "abort" "$3"
	return 1
fi	
if [ ! -z $lname ]; then
	if [ -z $(isinstalled $4) ]; then
		if [ $autoconfirm = "0" ]; then
			myecho $lang "libNotInstalled" "$4"
			myecho $lang "~install"
			if [ "$key" = "A" ] || [ "$key" = "a" ]; then
				autoconfirm="1"
			elif [ "$key" = "Y" ] || [ "$key" = "y" ]; then
				confirm="1"
			else 
				confirm="0"
			fi
		fi
		if [ $autoconfirm = "1" ] || [ $confirm = "1" ]; then
			# actual Install 
			myecho $lang "installing" "$4" "$cmd"
			myecho $lang "patient"
			$cmd >\dev\null
			
		else
			myecho $lang "skip" "$4"
		fi
	fi
fi

}


#------------------------ Main --------------------------
autoconfirm="0"
scriptpath=$(getpath)
lang=$(getlang)
bits=$(getbits)
libinst=$(getlibinst)
distro=$(getdistro)

echo -e ""
echo -e "${blue}-----------------------------------------------------${NC}"
echo -e ""
myecho $lang "installTitle" "$prgname"
echo -e ""
echo -e "${blue}(c) Myriad 2018                     Installer v1.1.0${NC}"
echo -e "${blue}-----------------------------------------------------${NC}"
echo -e ""


if [ $verbose = "1" ]; then
	echo -e "${green}path: $scriptpath"
	echo -e "distro: $distro"
	echo -e "bits: $bits"
	echo -e "lang: $lang"
	echo -e "inst: $libinst"
	echo -e "p1:   $prgname"
	echo -e "p2:   $dskname"
	echo -e "p3:   $icnname"
	echo -e "p4:   $mimetype"
	echo -e "p5:   $fontfolder${NC}"
fi

if [ $libinst = "apt-get" ] && [ $bits = "64" ]; then
	result=$(dpkg --print-foreign-architectures | grep -w i386)
	if [ -z "$result" ]; then
		myecho $lang "Architecture" 
		$(dpkg --add-architecture i386 >\dev\null)
		$(apt-get -y update >\dev\null)
		$(apt-get -y install libc6:i386 >\dev\null)
		myecho $lang "done"
	fi
fi

myecho $lang "copy"
cp -r $scriptpath/../InstallFiles/usr / &>/dev/nul
myecho $lang "done"

if [ "$prgname" = "PDFtoMusic Pro" ]; then
	myecho $lang "p2mp"
	ln -s "$prgname" "/usr/bin/p2mp"
	myecho $lang "done"
fi

if [ ! -z $fontfolder ]; then
	myecho $lang "regfont"
	"fc-cache -f -v /usr/share/fonts/truetype/$fontfolder" &>/dev/null && myecho $lang "error"
	myecho $lang "done"
fi

if [ ! -z $dskname ] && [ ! -z $mimetype ]; then
	myecho $lang "regfile"
	xdg-mime install --mode system --novendor "$scriptpath/../InstallFiles/myriad-files.xml" &>/dev/null
	myecho $lang "done"
fi
if [ ! -z $dskname ] && [ ! -z $mimetype ]; then
	myecho $lang "regmime"
	xdg-mime default "/usr/share/applications/$dskname" "application/$mimetype"  &>/dev/null 
	xdg-icon-resource install --mode system --novendor --context mimetypes --size 64 "/usr/share/pixmaps/$icnname" "application-$mimetype" &>/dev/null
	myecho $lang "done"
	myecho $lang "shortcut"
	xdg-desktop-menu install --novendor "/usr/share/applications/$dskname" &>/dev/null
	myecho $lang "done"
fi
# Dependencies
myecho $lang "depend"
# Special case of PCLinuxOS - 64
if [ $distro = "PCLinuxOS" ] && [ $bits = "64" ]; then
	#Check repository
	already="0"
	while read line; do
		if [[ $line =~ ^[^#].*32bit.* ]]; then
			already="1"
		fi
	done < /etc/apt/sources.list
	if [ $already = "0" ]; then
		myecho $lang "pclinuxos1"
		myecho $lang "pclinuxos2"
		myecho $lang "pclinuxos3"
		myecho $lang "~proceed"
		if [ "$key" = "Y" ] || [ "$key" = "y" ] ||  [ "$key" = "O" ] || [ "$key" = "o" ]; then
			echo "rpm http://ftp.nluug.nl/pub/os/Linux/distr/pclinuxos/pclinuxos/apt pclinuxos/32bit main updates" >> /etc/apt/sources.list
			`apt-get update`
		fi
	fi
	checkinstlib "$lang" 32 "$libinst" ia32-libs ia32-libs ia32-libs ia32-libs ia32-libs ia32-libs
	checkinstlib "$lang" 32 "$libinst" libexpat libexpat1 libexpat1 libexpat1 libexpat1 libexpat1
# All cases
else
	# checkinstlib <lang> <bits> <installer> <libname> <apt-get pkg> <yum pkg> <zypper pkg> <urpmi pkg> <pacman pkg>
	checkinstlib "$lang" "$bits" "$libinst" libc6 libc6 glibc glibc "" glibc
	checkinstlib "$lang" "$bits" "$libinst" libasound libasound2 alsa-plugins* libasound2 libalsa2 ""
	checkinstlib "$lang" "$bits" "$libinst" libfontconfig libfontconfig1 fontconfig fontconfig libfontconfig1 fontconfig
	checkinstlib "$lang" "$bits" "$libinst" libfreetype libfreetype6 freetype freetype2 libfreetype6 freetype2
	checkinstlib "$lang" "$bits" "$libinst" libX11 libx11-6 libX11 libX11-6 libx11_6 libx11
	checkinstlib "$lang" "$bits" "$libinst" libXrender libxrender1 libXrender libXrender1 libxrender1 libxrender
	checkinstlib "$lang" "$bits" "$libinst" libexpat "" "" "" libexpat1 ""
	checkinstlib "$lang" "$bits" "$libinst" libz zlib1g zlib libz1 zlib1 zlib
	# pb suse (zypper)
	#checkinstlib $lang $bits $libinst libpng12 libpng12-0 libpng libpng12-0 libpng3 libpng
	checkinstlib $lang $bits $libinst libxcb libxcb1 libxcb libxcb1 libxcb1 libxcb
fi
myecho $lang "done"

	
