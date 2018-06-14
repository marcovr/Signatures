#!/usr/bin/env bash
inputFile=$1
n=$2
outputFile=${3}out.txt

cd /input/

mkdir data
unzip -d data $inputFile

java -jar gedwrapper.jar -fr $n -o $outputFile data
