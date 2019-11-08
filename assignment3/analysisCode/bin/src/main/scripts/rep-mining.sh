#!/bin/bash

#INSERT YOUR GIT REPOSITORY BELOW
GIT="https://github.com/apache/commons-math.git"

#INSERT YOUR TARGET CSV FILE BELOW
DATASTORE="../log.csv"

#INSERT THE ROOT DIRECTORY OF THE REPO YOU ARE ANALYSING BELOW
REPO_DIR="commons-math"

#HOW MANY COMMITS DO YOU WANT TO ENCOMPASS IN YOUR ANALYSIS?
NUM_VERSIONS="100"

# Clone target GIT repository
git clone --single-branch  $GIT

cd $REPO_DIR

# Check out the current head from the git repository
# git checkout master

echo "Timestamp, Message, Committer, LOC" > $DATASTORE

#Obtain an array of hash-codes for the previous versions.
versions=($(git log master --no-walk --tags --pretty=format:"%h" -$NUM_VERSIONS ))

#Obtain the number of elements in the array.
num=${#versions[@]}

#Iterate through the array of versions, and for each version do:
for j in $(seq 1 $num); do

    #Store the hash code of the current version as i
    i=${versions[$j]}

    #Obtain the source code for the current version i by checking it out.
    git checkout -f $i

    #Obtain the date that version i was committed (via git show)
	DATE=$(git show -s --format='%ct' $i)

    #Obtain the commit message for the current version (also via git show)
    MESSAGE=$(git show -s --format='%B' $i | tr -d '[:punct:]\r\n')

    #Obtain the author ID for the current version (also via git show)
    AUTHOR=$(git show -s --format='%an' $i)

    #Obtain the total LOC in the source directory.
    #(This is where you can include whatever code you want to execute on the repository.)

    #If you want to carry out some form of static / dynamic analysis that requires acces to
    #the bytecode, you will probably need to run `mvn clean package` at this point...

    #In this example, we only use the raw source code, so no build is required.S
    LOC=$(find src -name '*.java' | xargs wc -l | awk {'print $1'} | tail -n1)

    #Pipe the various details that you have collected into the CSV file.
    echo "\"$DATE\", \"$MESSAGE\", \"$AUTHOR\", $LOC" >> $DATASTORE

done
