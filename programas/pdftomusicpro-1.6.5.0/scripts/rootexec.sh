#!/bin/bash

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

function echotrans
{
if [ "$1" = "fr" ] || [ "$1" = "FR" ] || [ "$1" = "Fr" ]; then
	echo -e "$3"
else
	echo -e "$2"
fi
}

# Translated echo : lang, index
function myecho
{
echlang=$1
set $2
case "$1" in
	rootpwd)
	echotrans $echlang "${cyan}In order to install, the ${NC}root password${cyan} must be entered.${NC}" "${cyan}Pour installer, le mot de passe ${NC}root${cyan} doit être entré.${NC}"
	;;
	err1)
	echotrans $echlang "${red}** ERROR ** Couldn't find a way to run as root. Aborted." "${red}** ERREUR ** Impossible d'exécuter en tant que root. Abandonné."
	;;
	err2)
	echotrans $echlang "Please perform a manual install.${NC}" "Veuillez procéder à une installation manuelle.${NC}"
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

# distrib name? (incomplete)
function getdistro
{
if [ ! -f "/etc/lsb-release" ]; then
	echo "Other"
else
	`grep -q Ubuntu /etc/lsb-release`
	if [ $? -eq 0 ]; then	
		echo "Ubuntu" 
	else
		echo "Other"
	fi
fi
}
PATH=$PATH:/bin:/usr/bin:/usr/local/bin:/sbin:/usr/sbin:/usr/local/sbin:/usr/X11/bin

command=$1

distro=$(getdistro)
lang=$(getlang)

#--- 1 - pkexec
rootcmd="`dowhich pkexec`"
if [ ! -z $rootcmd ]; then
	eval "$rootcmd $command"  && ret=1
	exit $ret
fi

#--- 2 - Sudo on Ubuntu
rootcmd="`dowhich sudo`"
if [ $distro = "Ubuntu" ] && [ ! -z "$rootcmd" ]; then
	myecho $lang "rootpwd"
	eval "$rootcmd $command" && ret=1
	exit $ret
fi

#--- 2 - Su
rootcmd="`dowhich su`"
if [ ! -z $rootcmd ]; then
	myecho $lang "rootpwd"
     	eval "$rootcmd  - root -c  $command"  && ret=1
	exit $ret
fi
myecho $lang "err1"
myecho $lang "err2"
exit 1
