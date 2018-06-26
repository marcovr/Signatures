#!/usr/bin/env bash
inputFile="$1"
r="$2"
g="$3"
v="$4"
outputFile="${5}results.json"

cd /input/ || exit

mkdir data
# unzip, if necessary
if [ "${inputFile##*.}" = "zip" ]; then
	unzip -d data "$inputFile"

	# v=false -> don't apply -v option
	# v=true -> apply -v option
	if [ "$v" = "true" ]; then
		mkdir verify
		for last_file in data/*; do true; done
		v_name=$(basename "$last_file")
		v_file="verify/$v_name"
		mv "$last_file" "$v_file"

		python run.py -r "$r" -g "$g" -s -j "$outputFile" -v "$v_file" data
		exit
	fi
else
	cp "$inputFile" data
fi

python run.py -r "$r" -g "$g" -s -j "$outputFile" data
