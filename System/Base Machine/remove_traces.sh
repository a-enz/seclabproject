#!/bin/bash

# Remove traces of development and setup:
# - clean shell history
# - empty log files

function empty_files {
  echo "Checking directory $1"
  for file in $1/*
  do
  echo "processing item: $file"
    if [ -f "$file" ]; then
      echo "...emptying file"
      > "$file"
    elif [ -d "$file" ]; then
      empty_files "$file"
    fi
  done
}


LOG_DIR="/var/log"

# clear out log files
echo "Recursively empty out all files starting with $LOG_DIR as root directory?"
echo "Type 'yes' to proceed"

read ANS

if [ "$ANS" == "yes" ]; then
  empty_files "$LOG_DIR"
else
  echo "Skipping log files. No log files emptied"
fi




# delete shell history, for this to work the script
# has to be sourced ('. ./remove_traces.sh' or 'source remove_traces')
echo "Deleting shell history only works if you sourced this cript"
echo "Type 'yes' if you sourced this script and want shell history deleted"

read ANS

if [ "$ANS" == "yes" ]; then
  history -c
  history -w
fi


# Set secure passwords for all users (root, iadmin, coreca)
passwd iadmin
passwd root
echo "SET PASSWORD FOR UNPRIVILEGED USER (database, coreca or webserver)"
