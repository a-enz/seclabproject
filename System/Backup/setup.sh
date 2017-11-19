#!/bin/bash
# Run this in the home directory of the backup user

umask 0077
###############
## FUNCTIONS ##
###############

# start cronjob for a certain machine
# arguments: script name, backup directory, file list, ssh target, offset, interval, log
function initiate_cronjob {
echo "Initiating cronjob for $2"
# create backup script
# don't indent this, otherwise EOF is not sent correctly:
cat << EOF > $1
echo "\n---------------------------"
echo "[\`date +%d-%m-%y/%H:%M:%S\`] running backup for $2:"
rsync -avzr -e ssh --files-from=$3 $4:/ $2/backup_\`date +%d%m%y_%H%M\`
EOF

# add backup script to crontab and pipe output to log file
echo "$5-59/$6 * * * * $1 >> $7 2>&1" >> tmpcron
}


# send ssh public key to host
# arguments: ssh target, key
function send_key {
echo "... at $1"
ssh-copy-id -i $2 $1
}


###############
## VARIABLES ##
###############

# naming of backup directories
WS_DIR=$HOME/web_server
FW_DIR=$HOME/firewall
CA_DIR=$HOME/core_ca
DB_DIR=$HOME/database

# naming of file lists
WS_LIST_1=$HOME/.ws_files_1
FW_LIST_1=$HOME/.fw_files_1
CA_LIST_1=$HOME/.ca_files_1
DB_LIST_1=$HOME/.db_files_1

WS_LIST_2=$HOME/.ws_files_2
FW_LIST_2=$HOME/.fw_files_2
CA_LIST_2=$HOME/.ca_files_2
DB_LIST_2=$HOME/.db_files_2

# Backup pull interval and offset(in min) TODO: increase for prod env
WS_OFFSET_1=1 ; WS_INTERVAL_1=20
FW_OFFSET_1=6 ; FW_INTERVAL_1=20
CA_OFFSET_1=11 ; CA_INTERVAL_1=20
DB_OFFSET_1=16 ; DB_INTERVAL_1=20

WS_OFFSET_2=1 ; WS_INTERVAL_2=5
FW_OFFSET_2=2 ; FW_INTERVAL_2=5
CA_OFFSET_2=3 ; CA_INTERVAL_2=5
DB_OFFSET_2=4 ; DB_INTERVAL_2=5

# Backup script names
WS_SH=$HOME/backup_ws.sh
FW_SH=$HOME/backup_fw.sh
CA_SH=$HOME/backup_ca.sh
DB_SH=$HOME/backup_db.sh

# ssh target
WS_ADR=iadmin@192.168.51.14
FW_ADR=root@192.168.50.50
CA_ADR=coreca@192.168.50.31
DB_ADR=database@192.168.50.33

# Backup log file
LOG=$HOME/backup.log

# SSH Key file
KEY=$HOME/.ssh/id_rsa



###########
## FILES ##
###########
echo "Creating backup ressources"
# create backup directories for each machine to back up
mkdir $WS_DIR $FW_DIR $CA_DIR $DB_DIR


echo "Creating list of files and directories to back up"
# First pull frequency
# web server
# TODO files
cat << EOF > $WS_LIST_1
/home/iadmin/test
EOF

# firewall
# TODO files
cat << EOF > $FW_LIST_1
/cf/conf/config.xml
EOF

# core ca
# TODO files
cat << EOF > $CA_LIST_1
/home/coreca
EOF

# database
# TODO files
cat << EOF > $DB_LIST_1
/home/database
EOF



# Second pull frequency

# # TODO files
# cat << EOF > $WS_LIST_2
# /home/iadmin/test
# EOF

# # firewall
# # TODO files
# cat << EOF > $FW_LIST_2
# /cf/conf/config.xml
# EOF

# # core ca
# # TODO files
# cat << EOF > $CA_LIST_2
# /home/coreca
# EOF

# # database
# # TODO files
# cat << EOF > $DB_LIST_2
# /home/database
# EOF



##############
## SSH KEYS ##
##############

# Distributes ssh public key of backup
if [ ! -f $KEY ]; then
echo "Creating ssh key, please don't change defaults (just hit enter)"
ssh-keygen
fi

# Copying ssh public key to machines we want to back up
echo "Copying ssh public key to remote hosts"

send_key $WS_ADR $KEY

# CAREFUL: to make key authorization persisten on pfsense, 
# it has to be added to the config.xml. Otherwise it will
# be wiped on reboot of the firewall
send_key $FW_ADR $KEY

send_key $CA_ADR $KEY

send_key $DB_ADR $KEY




##################
## CRONTAB JOBS ##
##################
echo "Starting cronjobs"
# (has to be executed in the shell of intended crontab user and not root)

# create files
touch $WS_SH $FW_SH $CA_SH $DB_SH
chmod u+x $WS_SH $FW_SH $CA_SH $DB_SH
touch $LOG tmpcron

# initiate cronjobs
initiate_cronjob $WS_SH $WS_DIR $WS_LIST_1 $WS_ADR $WS_OFFSET_1 $WS_INTERVAL_1 $LOG

initiate_cronjob $FW_SH $FW_DIR $FW_LIST_1 $FW_ADR $FW_OFFSET_1 $FW_INTERVAL_1 $LOG

initiate_cronjob $CA_SH $CA_DIR $CA_LIST_1 $CA_ADR $CA_OFFSET_1 $CA_INTERVAL_1 $LOG

initiate_cronjob $DB_SH $DB_DIR $DB_LIST_1 $DB_ADR $DB_OFFSET_1 $DB_INTERVAL_1 $LOG



# load rules into crontab
crontab < tmpcron
rm -f tmpcron

# restart crontab to make changes persist after reboot
# systemctl restart crond



######################
## INSTALL BACKDOOR ##
######################

# echo "Installing backdoor scripts, make sure install_backdoor.sh file is available"
# bash install_backdoor.sh


