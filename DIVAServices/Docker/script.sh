#!/usr/bin/env bash
inputFile="$1"
keepEdges="$2"
method="$3"
k="$4"
mergeMode="$5"
outputFile="${6}transformed"

cd /input/

mkdir data
mkdir out
# unzip, if necessary
if [ "${inputFile: -4}" == ".zip" ]; then
	unzip -d data "$inputFile"
else
	cp "$inputFile" data
fi

for inFile in data/*; do
	base=$(basename "$inFile")
	outFile="out/$base"
	
	java -jar /input/graphtransformer.jar transform "$inFile" "$outFile" "$keepEdges" "$method" "$k" "$mergeMode"
done

# rezip, if necessary
if [ "${inputFile: -4}" == ".zip" ]; then
	zip -j "${outputFile}.zip" out/*
else
	mv out/* "${outputFile}.xml"
fi
