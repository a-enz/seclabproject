# Install mysql
echo "Install MySQL"
# see https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-centos-7
wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm
sudo rpm -ivh mysql57-community-release-el7-9.noarch.rpm
sudo yum install mysql-server

# Run mysql service
echo "Start MySQL"
sudo systemctl start mysqld
sudo systemctl status mysqld

# Create new database
echo "Create new database"
mysql -u root < create database iMoviesDB;
mysql -u root < use iMoviesDB;

# Import imovies_users.dump in create db
echo "Import dumped data"
mysql -u root < source db_backup.dump;

# Configure MySQL
echo "Configure MySQL"
# Generate temporary password
sudo grep 'temporary password' /var/log/mysqld.log
# Change default settings
sudo mysql_secure_installation


