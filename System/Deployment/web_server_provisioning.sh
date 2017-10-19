# From https://www.digitalocean.com/community/tutorials/how-to-install-python-3-and-set-up-a-local-programming-environment-on-centos-7

# Update yum (CentOS package manager)
echo "Update yum"
sudo yum -y update

# Install yum-utils, a collection of utilities and plugins that extend and 
# supplement yum
echo "Install yum yum-utils"
sudo yum -y install yum-utils

# Install the CentOS Development Tools, which are used to allow you to build 
# and compile software from source code
echo "Install Development Tools"
sudo yum -y groupinstall development

# Install IUS (Inline with Upstream Stable), 
echo "Install IUS"
sudo yum -y install https://centos7.iuscommunity.org/ius-release.rpm

# Install most recent version of python
echo "Install latest python 3 version"
sudo yum -y install python36u

# Output version
python3.6 -V

# Install pip
echo "Install pip3"
sudo yum -y install python36u-pip

# Install Django
echo "Install Django"
sudo pip3.6 install Django

# Inject ssh private key to be able to read from repository
INSECURE_PRIVATE_KEY=$(</vagrant/id_rsa) 
echo "$INSECURE_PRIVATE_KEY" >> /home/vagrant/.ssh/id_rsa
chmod 400 /home/vagrant/.ssh/id_rsa

# Explicitly add private key to ssh
ssh-add /home/vagrant/.ssh/id_rsa

# Inject gitlab ECDSA fingerprint to known_hosts to be able to connect directly
ECDSA_FINGERPRINT=$(</vagrant/gitlab_ecdsa_fingerprint) 
echo "$ECDSA_FINGERPRINT" >> /home/vagrant/.ssh/known_hosts

# Sparse clone needed files from git repository
# for more info see https://stackoverflow.com/questions/600079/how-do-i-clone-a-subdirectory-only-of-a-git-repository/13738951#13738951
mkdir -p ~/SecLabProject
cd ~/SecLabProject
git init
git remote add -f origin git@gitlab.vis.ethz.ch:abaehler/SecLabProject.git
git config core.sparseCheckout true
echo "System/Core CA" >> .git/info/sparse-checkout
git pull origin master