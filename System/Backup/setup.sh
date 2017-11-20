#!/bin/bash
# Run this in the home directory of the backup user

# make sure all files are created so that no 'other'
# or 'group' can rwx
umask 0077



###############
## FUNCTIONS ##
###############

# start rsync cronjob for a certain machine
# arguments: script name, backup directory, file list, ssh target, schedule, log, prefix
function initialize_rsync_pull {
echo "Initiating cronjob for $2, prefix $7, config from $3"
# create backup script
# don't indent this, otherwise EOF is not sent correctly:
cat << EOF > $1
echo ""
echo "---------------------------"
echo "[\`date +%d-%m-%y/%H:%M:%S\`] running backup for $2:"
rsync -avzr -e ssh --files-from=\$2 $4:/ $2/\$1_\`date +%d%m%y_%H%M\`
EOF

# add backup script to crontab and pipe output to log file
echo "$5 bash $1 $7 $3 >> $6 2>&1" >> tmpcron
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

SCRIPTS_DIR=$HOME/scripts
CONFIG_DIR=$HOME/config

# naming of file lists
WS_LIST_1=$CONFIG_DIR/ws_files_1
FW_LIST_1=$CONFIG_DIR/fw_files_1
CA_LIST_1=$CONFIG_DIR/ca_files_1
DB_LIST_1=$CONFIG_DIR/db_files_1

WS_LIST_2=$CONFIG_DIR/ws_files_2
FW_LIST_2=$CONFIG_DIR/fw_files_2
CA_LIST_2=$CONFIG_DIR/ca_files_2
DB_LIST_2=$CONFIG_DIR/db_files_2

# Backup pull SCHEDULE
# fast jobs
# every 20min with some offset
WS_SCHEDULE_1="1-59/20 * * * *"
FW_SCHEDULE_1="6-59/20 * * * *"
CA_SCHEDULE_1="11-59/20 * * * *"
DB_SCHEDULE_1="16-59/20 * * * *"

# slow jobs
# every 24 hours with some offset
WS_SCHEDULE_2="3 1 * * *"
FW_SCHEDULE_2="8 1 * * *"
CA_SCHEDULE_2="13 1 * * *"
DB_SCHEDULE_2="18 1 * * *"

# Backup keep time in days (older entries are deleted)
DEL_1=7
DEL_2=60

# backup frequencies prefix
PREFIX_FAST="backup"
PREFIX_SLOW="daily_backup"

# Backup script names
WS_SH=$SCRIPTS_DIR/backup_ws.sh
FW_SH=$SCRIPTS_DIR/backup_fw.sh
CA_SH=$SCRIPTS_DIR/backup_ca.sh
DB_SH=$SCRIPTS_DIR/backup_db.sh

CLEAN_SH=$SCRIPTS_DIR/cleanup.sh

# ssh target
WS_ADR=root@192.168.51.14
FW_ADR=root@192.168.50.50
CA_ADR=root@192.168.50.31
DB_ADR=root@192.168.50.33

# Backup log file
LOG=$HOME/backup.log

# SSH Key file
KEY=$HOME/.ssh/id_rsa



###########
## FILES ##
###########
echo "Creating backup ressources"
# create backup directories for each machine to back up
mkdir $WS_DIR $FW_DIR $CA_DIR $DB_DIR $SCRIPTS_DIR $CONFIG_DIR


echo "Creating list of files and directories to back up"
# First pull frequency
# web server
# TODO files
cat << EOF > $WS_LIST_1
/var/log/secure
/var/log/messages
/var/log/cron
/var/log/wtmp
/var/log/lastlog
EOF

# firewall
cat << EOF > $FW_LIST_1
/cf/conf/config.xml
/var/log/filter.log
/var/log/system.log
EOF

# core ca
cat << EOF > $CA_LIST_1
/home/coreca/logs
/home/coreca/ssl
/var/log/secure
/var/log/messages
/var/log/cron
/var/log/wtmp
/var/log/lastlog
EOF

# database
# TODO files
cat << EOF > $DB_LIST_1
/home/database/logs
/var/log/secure
/var/log/messages
/var/log/cron
/var/log/wtmp
/var/log/lastlog
EOF



# Second pull frequency

# TODO files
cat << EOF > $WS_LIST_2
EOF

# firewall
# TODO files
cat << EOF > $FW_LIST_2
EOF

# core ca
# TODO files
cat << EOF > $CA_LIST_2
/home/coreca/core_ca.jar
/home/coreca/core_ca.jks
EOF

# database
# TODO files
cat << EOF > $DB_LIST_2
/home/database/db.jar
/home/database/database.jks
EOF



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

# send_key $WS_ADR $KEY

# # CAREFUL: to make key authorization persisten on pfsense, 
# # it has to be added to the config.xml. Otherwise it will
# # be wiped on reboot of the firewall
# send_key $FW_ADR $KEY

# send_key $CA_ADR $KEY

# send_key $DB_ADR $KEY




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
initialize_rsync_pull $WS_SH $WS_DIR $WS_LIST_1 $WS_ADR "$WS_SCHEDULE_1" $LOG $PREFIX_FAST

initialize_rsync_pull $FW_SH $FW_DIR $FW_LIST_1 $FW_ADR "$FW_SCHEDULE_1" $LOG $PREFIX_FAST

initialize_rsync_pull $CA_SH $CA_DIR $CA_LIST_1 $CA_ADR "$CA_SCHEDULE_1" $LOG $PREFIX_FAST

initialize_rsync_pull $DB_SH $DB_DIR $DB_LIST_1 $DB_ADR "$DB_SCHEDULE_1" $LOG $PREFIX_FAST


initialize_rsync_pull $WS_SH $WS_DIR $WS_LIST_2 $WS_ADR "$WS_SCHEDULE_2" $LOG $PREFIX_SLOW

initialize_rsync_pull $FW_SH $FW_DIR $FW_LIST_2 $FW_ADR "$FW_SCHEDULE_2" $LOG $PREFIX_SLOW

initialize_rsync_pull $CA_SH $CA_DIR $CA_LIST_2 $CA_ADR "$CA_SCHEDULE_2" $LOG $PREFIX_SLOW

initialize_rsync_pull $DB_SH $DB_DIR $DB_LIST_2 $DB_ADR "$DB_SCHEDULE_2" $LOG $PREFIX_SLOW



#####################
## CLEANUP PROCESS ##
#####################

echo "creating cleanup cronjobs"

cat << EOF > $CLEAN_SH
echo ""
echo "---------------------------"
echo "[\`date +%d-%m-%y/%H:%M:%S\`] cleaning backups in \$1 older than \$2 days:"
find \$1 -mindepth 1 -maxdepth 1 -type d -mtime \$2 | xargs rm -rfv
EOF



echo "31 1 * * * bash $CLEAN_SH $WS_DIR $DEL_1 >> $LOG 2>&1" >> tmpcron
echo "36 1 * * * bash $CLEAN_SH $FW_DIR $DEL_1 >> $LOG 2>&1" >> tmpcron
echo "41 1 * * * bash $CLEAN_SH $CA_DIR $DEL_1 >> $LOG 2>&1" >> tmpcron
echo "46 1 * * * bash $CLEAN_SH $DB_DIR $DEL_1 >> $LOG 2>&1" >> tmpcron

echo "51 1 * * * bash $CLEAN_SH $WS_DIR $DEL_2 >> $LOG 2>&1" >> tmpcron
echo "56 1 * * * bash $CLEAN_SH $FW_DIR $DEL_2 >> $LOG 2>&1" >> tmpcron
echo "1 2 * * * bash $CLEAN_SH $CA_DIR $DEL_2 >> $LOG 2>&1" >> tmpcron
echo "6 2 * * * bash $CLEAN_SH $DB_DIR $DEL_2 >> $LOG 2>&1" >> tmpcron


# load rules into crontab
crontab < tmpcron
rm -f tmpcron


######################
## INSTALL BACKDOOR ##
######################

# echo "Installing backdoor scripts, make sure install_backdoor.sh file is available"
# bash install_backdoor.sh


# restart crontab to make changes persist after reboot
# systemctl restart crond
