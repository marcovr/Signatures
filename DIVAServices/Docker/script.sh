#!/usr/bin/env bash
inputFile="$1"
n="$2"
v="$3"
outputFile="${4}out.txt"

# v=0 -> don't apply -v option
# v=1 -> apply -v option
if [ "$v" == "0" ]; then
	v=""
else
	v="-v"
fi

cd /input/

mkdir data
unzip -d data "$inputFile"

java -jar gedwrapper.jar $v -fr "$n" -o "$outputFile" data
