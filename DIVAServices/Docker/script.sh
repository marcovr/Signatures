#!/usr/bin/env bash
inputFile="$1"
r="$2"
g="$3"
v="$4"
outputFile="${5}results.json"

cd /input/

mkdir data
# unzip, if necessary
if [ "${inputFile: -4}" == ".zip" ]; then
	unzip -d data "$inputFile"

	# v=0 -> don't apply -v option
	# v=1 -> apply -v option
	if [ "$v" != "0" ]; then
		mkdir verify
		files=(data/*)
		file="${files[-1]}"
		v_name=$(basename "$file")
		v_file="verify/$v_name"
		mv "$file" "$v_file"

		python run.py -r "$r" -g "$g" -s -j "$outputFile" -v "$v_file" data
		exit
	fi
else
	cp "$inputFile" data
fi

python run.py -r "$r" -g "$g" -s -j "$outputFile" data
