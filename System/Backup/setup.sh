#!/bin/bash
# Run this in the home directory of the backup user

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
WS_LIST=$HOME/web_server_files
FW_LIST=$HOME/firewall_files
CA_LIST=$HOME/core_ca_files
DB_LIST=$HOME/database_files

# Backup pull interval and offset(in min) TODO: increase for prod env
WS_OFFSET=1 ; WS_INTERVAL=5
FW_OFFSET=2 ; FW_INTERVAL=5
CA_OFFSET=3 ; CA_INTERVAL=5
DB_OFFSET=4 ; DB_INTERVAL=5


# Backup script names
WS_SH=$HOME/backup_ws.sh
FW_SH=$HOME/backup_fw.sh
CA_SH=$HOME/backup_ca.sh
DB_SH=$HOME/backup_db.sh

# ssh target
WS_ADR=iadmin@192.168.51.14
FW_ADR=root@192.168.50.50
CA_ADR=iadmin@192.168.50.31
DB_ADR=iadmin@192.168.50.33

# Backup log file
LOG=$HOME/backup.log

# SSH Key file
$KEY=$HOME/.ssh/ip_rsa




###########
## FILES ##
###########
echo "Creating backup ressources"
# create backup directories for each machine to back up
mkdir $WS_DIR $FW_DIR $CA_DIR $DB_DIR


echo "Creating list of files and directories to back up"
# web server
# TODO files
cat << EOF > $WS_LIST
/home/iadmin/test
EOF

# firewall
# TODO files
cat << EOF > $FW_LIST
/cf/conf/config.xml
EOF

# core ca
# TODO files
cat << EOF > $CA_LIST
/home/iadmin/test
EOF

# database
# TODO files
cat << EOF > $DB_LIST
/home/iadmin/test
EOF




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
initiate_cronjob $WS_SH $WS_DIR $WS_LIST $WS_ADR $WS_OFFSET $WS_INTERVAL $LOG

initiate_cronjob $FW_SH $FW_DIR $FW_LIST $FW_ADR $FW_OFFSET $FW_INTERVAL $LOG

initiate_cronjob $CA_SH $CA_DIR $CA_LIST $CA_ADR $CA_OFFSET $CA_INTERVAL $LOG

initiate_cronjob $DB_SH $DB_DIR $DB_LIST $DB_ADR $DB_OFFSET $DB_INTERVAL $LOG



# load rules into crontab
crontab < tmpcron
rm -f tmpcron

# restart crontab to make changes persist after reboot
systemctl restart crond



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


# TODO: cleanup process
##############


