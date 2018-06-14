#!/usr/bin/env bash
inputFile=$1
r=$2
g=$3
outputFile=${4}out.txt

cd /input/

mkdir data
if [ ${inputFile: -4} == ".zip" ]; then
	unzip -d data $inputFile
else
	cp $inputFile data
fi

python run.py -r $r -g $g data > $outputFile
