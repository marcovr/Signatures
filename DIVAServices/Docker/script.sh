#!/usr/bin/env bash
inputFile="$1"
outputFile="${2}imageGraph"

cd /input/ || exit

mkdir data
mkdir out
# unzip, if necessary
if [ "${inputFile##*.}" = "zip" ]; then
	unzip -d data "$inputFile"
else
	cp "$inputFile" data
fi

for inFile in data/*; do
	base=$(basename "$inFile")
	outFile="out/${base%.*}.xml"
	
	python3 run.py -i "$inFile" -o "$outFile"
done

# rezip, if necessary
if [ "${inputFile##*.}" = "zip" ]; then
	zip -j "${outputFile}.zip" out/*
else
	mv out/* "${outputFile}.xml"
fi
