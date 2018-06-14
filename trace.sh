#!/bin/bash
source .env/bin/activate
python3 run.py -i "$1" -o "$2"
#python3 run.py -i "$1" -d -o "$2"
deactivate
