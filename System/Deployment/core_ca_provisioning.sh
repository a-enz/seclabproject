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
echo "Create CA directories and files"
# TODO: copy from setup.sh