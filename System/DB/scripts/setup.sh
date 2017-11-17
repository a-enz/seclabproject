# Install mysql
echo "Install MySQL"
# see https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-centos-7
#wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm
#rpm -ivh mysql57-community-release-el7-9.noarch.rpm
#yum install mysql-server

# Create keystore
#openssl pkcs12 -export -in ../Certificates/database.pem -inkey ../Certificates/database.key -out database.p12 -name iMovies -passout pass:passwordThatShouldNotBeHardcoded
#keytool -importkeystore -srckeystore database.p12 -srcstoretype pkcs12 -destkeystore database.jks -deststoretype JKS -storepass passwordThatShouldNotBeHardcoded
#keytool -import -trustcacerts -alias root -file ../Certificates/cacert.crt -keystore database.jks

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

cd ~/

cp ./DB/database.jks .
cp ./DB/out/artifacts/DB_jar/DB.jar ./db.jar
cp -r ./DB/scripts/ ./scripts

# TODO: cleanup
rm -r ./DB

# Prepare service (as root)
# cp /home/database/scripts/database.service /etc/systemd/system
# systemctl enable database
# systemctl start database