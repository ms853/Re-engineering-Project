#!/bin/bash

#INSERT YOUR GIT REPOSITORY BELOW
GIT="https://github.com/apache/commons-math.git"

#INSERT YOUR TARGET CSV FILE BELOW
DATASTORE="../diffs.csv"

#INSERT THE ROOT DIRECTORY OF THE REPO YOU ARE ANALYSING BELOW
REPO_DIR="commons-math"

#HOW MANY COMMITS DO YOU WANT TO ENCOMPASS IN YOUR ANALYSIS?
NUM_VERSIONS="50"

# Clone target GIT repository
git clone --single-branch  ${GIT}

cd $REPO_DIR

# Check out the current head from the git repository
# git checkout master

echo "Timestamp, Message, Committer, Added, Removed, File" > ${DATASTORE}

# Obtain an array of hash-codes for the previous versions.

VERSIONS=($(git log master --no-walk --tags --pretty=format:"%h" -$NUM_VERSIONS ))

# Iterate through the array of versions, and for each version do

for i in ${VERSIONS[@]}; do

    #Obtain the source code for the current version i by checking it out.
    git checkout -f $i

    #Obtain the date that version i was committed (via git show)
	  DATE=$(git show -s --format='%ct' $i)

    #Obtain the commit message for the current version (also via git show)
    MESSAGE=$(git show -s --format='%B' $i | tr -d '[:punct:]\r\n')

    #Obtain the author ID for the current version (also via git show)
    AUTHOR=$(git show -s --format='%an' $i)

    #Obtain individual file change data
    FILE_CHANGES=$(git show --numstat --format="%n")

    arr=()
    while read -r line; do
        check=$(echo "$line" | tr -d " \t\n\r")
        if [ "$check" != "" ]; then
            REPLACE_WITH_COMMA=${line//	/,}
            arr+=("$REPLACE_WITH_COMMA")
        fi
    done <<< "${FILE_CHANGES}"

    lines=${#arr[@]}

    for k in $(seq 0 "$((lines-1))"); do
        DIFF=${arr[$k]}
        echo $DIFF
        echo "\"$DATE\", \"$MESSAGE\", \"$AUTHOR\", "${DIFF} >> ${DATASTORE}
    done

done
