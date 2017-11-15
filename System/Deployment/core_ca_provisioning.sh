# TODO: Install Java version 9 (Oracle version)
echo "Install Java"
# see https://howtoprogram.xyz/2017/09/22/install-oracle-java-9-centos-rhel/
# Download from Oracle webpage
sudo wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" \
http://download.oracle.com/otn-pub/java/jdk/9+181/jdk-9_linux-x64_bin.rpm \
-O jdk-9_linux-x64_bin.rpm
# Install
rpm -ivh jdk-9_linux-x64_bin.rpm
# Set up env variables
export JAVA_HOME=/usr/java/jdk-9/
export PATH="$PATH:$JAVA_HOME/bin"
# Reload env
source /etc/environment

# Set up CA stuff as showed at page 109 in the book
echo "Setup"
cd ~/

# Clone repository
ssh-keyscan -H gitlab.vis.ethz.ch >> ~/.ssh/known_hosts
git clone git@gitlab.vis.ethz.ch:abaehler/SecLabProject.git

# FIXME: Change branch until not merged
git checkout rest_api

# Copy relevant data here
cp ./SecLabProject/System/Core\ CA/target .
cp ./SecLabProject/System/Core\ CA/scripts .
cp ./SecLabProject/System/Core\ CA/openssl.cnf .
cp ./SecLabProject/System/Certificates/cacert.pem .
cp ./SecLabProject/System/Certificates/cakey.pem .
cp ./SecLabProject/System/Certificates/cakeystore .


echo "Create CA directories and files"
mkdir ./ssl/CA
mkdir ./ssl/CA/certs
mkdir ./ssl/CA/newcerts
mkdir ./ssl/CA/private
mkdir ./ssl/CA/crl
bash -c "echo '01' > ./ssl/CA/crlnumber"
mkdir ./tmp
mkdir ./logs
bash -c "echo '01' > ./ssl/CA/serial"
touch ./ssl/CA/index.txt
mv ./cacert.pem ./ssl/CA
mv ./cakey.pem ./ssl/CA/private
mv ./openssl.cnf ./ssl


# TODO: Clean up (logs, history, ...)
rm -r ./SecLabProject

# TODO: start application

