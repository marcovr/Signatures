#!/usr/bin/env bash
inputFile=$1
keepEdges=$2
method=$3
k=$4
mergeMode=$5
outputFile=${6}transformed.xml

java -jar /input/graphtransformer.jar transform $inputFile $outputFile $keepEdges $method $k $mergeMode
