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

# Create keystore for https and activate secure option in code
#openssl pkcs12 -export -in ../Certificates/core_ca.pem -inkey ../Certificates/core_ca.key -out core_ca.p12 -name iMovies -passout pass:passwordThatShouldNotBeHardcoded
#keytool -importkeystore -srckeystore core_ca.p12 -srcstoretype pkcs12 -destkeystore core_ca.jks -deststoretype JKS -storepass passwordThatShouldNotBeHardcoded
#keytool -import -trustcacerts -alias root -file ../Certificates/cacert.crt -keystore ca_cert.jks

# TODO: Create startup service for the application
# put java -jar ~/coreca/core_ca.jar in /etc/init.d/start_core_ca and make it executable with chmod +x /etch/init.d/start_core_ca

## Log in as coreca:secure
## Copy files from host to coreca@192.168.50.31:~/coreca/ using scp

# Set up CA stuff
echo "Setup"
cd ~/

# Copy relevant data here
cp ./Core\ CA/out/artifacts/Core_CA_jar/Core\ CA.jar ./core_ca.jar
cp -r ./Core\ CA/scripts/ ./scripts
cp -r ./Core\ CA/ssl_base/ ./ssl
cp -r ./Core\ CA/ssl_base/ ./ssl_base
cp ./Core\ CA/core_ca.jks .


echo "Create CA directories and files"
mkdir ./tmp
mkdir ./logs

# TODO change permissions to allow only coreca user for any operation


# TODO: Clean up (logs, history, ...)
rm -r ./Core\ CA

# Prepare service (as root)
# cp /home/coreca/scripts/coreca.service /etc/systemd/system
# systemctl enable coreca
# systemctl start coreca