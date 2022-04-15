#!/bin/bash
# create startinglocations database script
#
# if you are on linux replace mac with linux
# this is not designed to run on windows
#
# you must run something like this first to create the database:
#
# ./mac/cblite --create startinglocations.cblite2   
#
# database filename - must already be created
dbFileName="startinglocations.cblite2"

# get every file that ends in json in an array 
# and the loop through each file
for i in *.json; do

	# get json as string
	json=`cat $i | tr -d '\'`

	# get id used for making document
	id=`cat $i | tr -d '\' | jq -r '.locationId'`

	# add file contents to the database
	./mac/cblite put --create $dbFileName $id "$json"
done

echo "validate documents were created\n"
./mac/cblite ls -l --limit 40 $dbFileName