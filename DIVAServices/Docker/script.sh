#!/usr/bin/env bash
inputFile=$1
outputFile=${2}imageGraph.xml

cd /input/

python3 run.py -i $inputFile -o $outputFile
