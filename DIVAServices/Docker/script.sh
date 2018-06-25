#!/usr/bin/env bash
inputFile="$1"
n="$2"
s="$3"
v="$4"
outputFile="${5}out.txt"

# v=0 -> don't apply -v option
# v=1 -> apply -v option
if [ "$v" = "0" ]; then
	v=""
else
	v="-v"
fi

# s=0 -> don't apply -f option
# s=1 -> apply -f option
if [ "$s" = "0" ]; then
	f=""
else
	f="-f"
fi

cd /input/ || exit

mkdir data
unzip -d data "$inputFile"

java -jar gedwrapper.jar $v $f -r "$n" -o "$outputFile" data
