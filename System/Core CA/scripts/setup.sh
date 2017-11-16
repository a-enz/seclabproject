# TODO: Install Java version 9 (Oracle version)
echo "Install Java"
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

# Insert MANIFEST in jar with
#jar uf out/artifac input-file(s) 

# Create startup service for the application
# put java -jar ~/coreca/core_ca.jar in /etc/init.d/start_core_ca and make it executable with chmod +x /etch/init.d/start_core_ca

## Log in as coreca:secure
## Copy files from host to coreca@192.168.50.31:~/coreca/ using scp

# Set up CA stuff
echo "Setup"
cd ~/coreca

# FIXME: Change branch until not merged
cd SecLabProject
git checkout rest_api
cd ..

# Copy relevant data here
cp ./SecLabProject/System/Core\ CA/out/artifacts/Core_CA_jar/Core\ CA.jar ./core_ca.jar
cp ./SecLabProject/System/Core\ CA/scripts .
cp ./SecLabProject/System/Core\ CA/ssl/ .
cp ./SecLabProject/System/Certificates/cakeystore .


echo "Create CA directories and files"
mkdir ./tmp
mkdir ./logs

# TODO change permissions to allow only coreca user for any operation


# TODO: Clean up (logs, history, ...)
rm -r ./SecLabProject

# TODO: start application
java -jar Core\ CA.jar