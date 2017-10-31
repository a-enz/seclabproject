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



# Provisioning
# create backup directory
echo "Authorize keys and create backup directory"
mkdir /home/vagrant/backup
chown vagrant:vagrant /home/vagrant/backup
# authorize ssh key of web_server machine
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCWse17nPQ/8bYN+W2fg37ea/++Y6T6rTkod/GN0MCb/LnEWhZe+H/Q/u3JDelH8/EFvQI/MmjTSKA+ttJn7hWiaLZ44p5N6sPtFul9V5saeMM6iVywrFGNFRhPeASF5PYvBmTQpOwgN4yLdfWY3AgTzxYF1uXQrztQ/eu3LOWiwINRqQG/rwieWgC6Bs+v0EcNLJ3r7YY70SHIcsIEBiQkHlxI75M79e2UCnLU5PTRn/GsGh/s1JeUEZi9yUbSMfzjnjAMuyg04SdFulAgFSXb0hCcXM8qTO4yUKEsPLRcNROywpkfH0fSVq3/pKRKZpjdYoVFGwC18la85xGatvbx vagrant@ict-networks-010-000-002-015.fwd-v4.ethz.ch" >> /home/vagrant/.ssh/authorized_keys
