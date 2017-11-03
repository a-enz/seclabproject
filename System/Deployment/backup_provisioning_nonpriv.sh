backup_user=$1
DIR=/home/${backup_user}/
BACKUP_DIR=${DIR}.backup/


######################################
# BACKUP PROCESS
######################################
echo "Configuring backup processes"

# Load Keys for ssh connection used in rsync
#===========================================
#
# Store Key files where needed
echo "Installing keys"
touch ${DIR}.ssh/id_rsa
chmod 600 ${DIR}.ssh/id_rsa
echo "-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEAlrHte5z0P/G2Dfltn4N+3mv/vmOk+q05KHfxjdDAm/y5xFoW
Xvh/0P7tyQ3pR/PxBb0CPzJo00igPrbSZ+4Vomi2eOKeTerD7RbpfVebGnjDOolc
sKxRjRUYT3gEheT2LwZk0KTsIDeMi3X1mNwIE88WBdbl0K87UP3rtyzlosCDUakB
v68InloAugbPr9BHDSyd6+2GO9EhyHLCBAYkJB5cSO+TO/XtlApy1OT00Z/xrBof
7NSXlBGYvclG0jH8454wDLsoNOEnRbpQIBUl29IQnFzPKkzuMlChLDy0XDUTssKZ
Hx9H0lat/6SkSmaY3WKFRRsAtfJWvOcRmrb28QIDAQABAoIBACDF730bxG8HtW1S
msm/Ql9DI6qXZzDxbWXkuA78oHcMPqZ6ZTUgXEPqvXIlaV+xqPBzi8KMxwl0WQ1q
m1XpDKF2noCp4H3Xmea9Peh5ngziyKq5ZwQxZccGGHRoCKvKANR3UoOh6oyTKCjI
bbJh4AvW1NZOR0rcYU0a/CGpTQPY/wdvhqhxcYiRRr7WTRLXpUSYCsfuRP3dicSj
gAa/BDv9HEiadujryos+KI48F3d77Nm25gIFSu8yNjw7qh/5cFWsJlH3sw6a/RP0
9oBFgd1D1j4xjfsv0UY7hyiDw1xo3byj32NMYboav0u/kei0K1ftn58LF0pHuuK6
/rKu/RUCgYEAxG+5un4+gZeI31n7L5sIzOP4EVuC/n4ETzXDIK5Ta33q2xNtw9c9
gXnGiBXhebqUmx4ItczJ9UvQuaHoP4jmP7CIJXcIhtggucq6nlRi3J7PsOFuDx/C
1gDhM0Qn+YpJPqaJq4t2fNZsrEVHQjOMHDOGOmwpPuYiZzyZKOEpCC8CgYEAxGOM
2YN/r+NUQku/nYvLcdKb7TalPx9EAFEa6wZc8HRRIr0VC9f7exTh3uuwc64AJG6R
lxVU0G7+MSpSUiMHTTW8U1x0jmPPQFPAxLj2e2hoorBSO9V8YcLQ84TGebCZZ+y+
V8KKwu3AkC/QpVlKT+cSmNKb9/5ryAOsl6wJCt8CgYEArg4pTbwK+p0g5HdAgMAo
nhPpfMfPXFygyObkTRLqS7a44QFoIz2W/CDA71gj/855WrDUGvKoiLdy7OkvdwyA
qDJtkHTWk1TyOm4NybkQGFiHwz0cXj5QT3tcECb2HMCRgffiyKO8i4UbqdGBAIHY
RizJZP7t1RPpq/wf4f8QcnUCgYBVsa7DNwsff83Dg/Qf1eE3UJQzhTXD/muYy8s4
JLWnQsooo2MsoNkeUvVgZW3AfHuZahnjWC6DKPuIDpSPPLOx8DG2GlDN1SWZRzKF
ZMRBA0UtQE0RyM0Wh4DP0e8dKH84BujjuIL+Hep5wDOGAxlXFlhaEQR3yS/Uvi2w
nSPw9wKBgHqu3KdOSwf7XQlaFP5VWos8ep5m6nrX3PmWwBXyn8tPc69SSAhGwmvZ
fWJOIisN6mg3zYamzMyE1sCDzIgNSE5ZJdMQCOQHHbtFphkyMM/UryQAdoMJRop/
knjlkMbVVNSGUoxcvmX3VaZCCX824MPAnPkJtWkk7Nh4YF23klkF
-----END RSA PRIVATE KEY-----" > ${DIR}.ssh/id_rsa

touch ${DIR}.ssh/id_rsa.pub
chmod 644 ${DIR}.ssh/id_rsa.pub
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCWse17nPQ/8bYN+W2fg37ea/++Y6T6rTkod/GN0MCb/LnEWhZe+H/Q/u3JDelH8/EFvQI/MmjTSKA+ttJn7hWiaLZ44p5N6sPtFul9V5saeMM6iVywrFGNFRhPeASF5PYvBmTQpOwgN4yLdfWY3AgTzxYF1uXQrztQ/eu3LOWiwINRqQG/rwieWgC6Bs+v0EcNLJ3r7YY70SHIcsIEBiQkHlxI75M79e2UCnLU5PTRn/GsGh/s1JeUEZi9yUbSMfzjnjAMuyg04SdFulAgFSXb0hCcXM8qTO4yUKEsPLRcNROywpkfH0fSVq3/pKRKZpjdYoVFGwC18la85xGatvbx vagrant@ict-networks-010-000-002-015.fwd-v4.ethz.ch" > ${DIR}.ssh/id_rsa.pub


# Backup locations
# ================
# create backup directory
echo "Creating backup ressources for web_server"
mkdir -p ${BACKUP_DIR}web_server


# Web Server backup pull config
# =============================
# 
# List files we need to back up on the web server
# CARE: list cannot be too long otherwise it might exceed argument limit
# check: https://stackoverflow.com/questions/9766284/read-input-from-a-file-and-sync-accordingly
echo "${DIR}example_dir/" > ${BACKUP_DIR}web_server_backup_list.txt

# start crontab with rsync 
# (has to be executed in the shell of intended crontab user and not root)
touch ${BACKUP_DIR}backup.sh
chmod u+x ${BACKUP_DIR}backup.sh
touch ${BACKUP_DIR}backup.log
echo "echo \"[\`date +%d-%m-%y/%H:%M:%S\`] starting backup process:\"" > ${BACKUP_DIR}backup.sh


echo "rsync -avz -e ssh --files-from=${BACKUP_DIR}web_server_backup_list.txt vagrant@192.168.50.30:/ ${BACKUP_DIR}web_server/backup_\`date +%d%m%y_%H%M\`" >> ${BACKUP_DIR}backup.sh

echo "* * * * * ${BACKUP_DIR}backup.sh >> ${BACKUP_DIR}backup.log 2>&1" | crontab

