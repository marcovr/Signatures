#!/usr/bin/env bash
inputFile="$1"
nReference="$2"
sortMode="$3"
verificationMode="$4"
nodeCost="$5"
edgeCost="$6"
useEdgeLength="$7"
outputFile="${8}out.csv"

# verificationMode=false -> don't apply -v option
# verificationMode=true -> apply -v option
if [ "$verificationMode" = "false" ]; then
	verificationMode=""
else
	verificationMode="-v"
fi

# sortMode=false -> don't apply -f option
# sortMode=true -> apply -f option
if [ "$sortMode" = "false" ]; then
	sortMode=""
else
	sortMode="-f"
fi

if [ "$useEdgeLength" = "false" ]; then
	edgeLabel="numOfEdgeAttr=0"
else
	edgeLabel="numOfEdgeAttr=1\\nedgeAttr0=length\\nedgeAttr0Importance=1\\nedgeCostType0=absolute"
fi

cd /input/ || exit

mkdir data
unzip -d data "$inputFile"

# apply cost function
sed -i -e 's/$NODECOST/node='"$nodeCost"'/' \
	-e 's/$EDGECOST/edge='"$edgeCost"'/' \
	-e 's/$EDGELABEL/'"$edgeLabel"'/' \
	default.prop

java -jar gedwrapper.jar $verificationMode $sortMode \
	-r "$nReference" -o "$outputFile" data
