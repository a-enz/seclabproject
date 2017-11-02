# TODO
# Installing the software for rsync backup:
# rsync: already on centos/7 box
# ssh: already on centos/7 box
# cron: already on centos/7 box

# Basic idea: backup with rsync:
# ==============================
# 1) generate ssh keypair, push pubkey to backup server
# 2) use rsync to push predefined files to backup server
# 3) do scheduled backup with crontab
# ssh-keygen create key (without passphrase)
# ssh-copy-id user@hostname.example.com:  give public key to backup server
# run crontab every 30min
# */30 * * * * /usr/bin/somedirectory/somecommand
# rsync -e ssh web_server vagrant@192.168.50.32:~/backup/

