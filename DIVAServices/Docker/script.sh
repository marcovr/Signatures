#!/usr/bin/env bash
inputFile="$1"
n="$2"
s="$3"
v="$4"
outputFile="${5}out.csv"

# v=false -> don't apply -v option
# v=true -> apply -v option
if [ "$v" = "false" ]; then
	v=""
else
	v="-v"
fi

# s=false -> don't apply -f option
# s=true -> apply -f option
if [ "$s" = "false" ]; then
	f=""
else
	f="-f"
fi

cd /input/ || exit

mkdir data
unzip -d data "$inputFile"

java -jar gedwrapper.jar $v $f -r "$n" -o "$outputFile" data
