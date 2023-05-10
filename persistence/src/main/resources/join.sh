#!/bin/bash

# Set the directory where the files are located
dir="./db/migration"

# Change to the directory
cd $dir

# Join all files in the directory (in name order) into a single file
cat $(ls -v) > temp.txt
