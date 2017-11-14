# From https://www.digitalocean.com/community/tutorials/how-to-install-python-3-and-set-up-a-local-programming-environment-on-centos-7

# Update yum (CentOS package manager)
echo "Update yum"
yum -y update

# Install yum-utils, a collection of utilities and plugins that extend and 
# supplement yum
echo "Install yum yum-utils"
yum -y install yum-utils

# Install the CentOS Development Tools, which are used to allow you to build 
# and compile software from source code
echo "Install Development Tools"
yum -y groupinstall development

# Install IUS (Inline with Upstream Stable), 
echo "Install IUS"
yum -y install https://centos7.iuscommunity.org/ius-release.rpm

# Install most recent version of python
echo "Install latest python 3 version"
yum -y install python36u

# Install pip
echo "Install pip3"
yum -y install python36u-pip

# Install some tools for network debugging
echo "Installing some network tools..."
echo "tcpdump"
yum -y install tcpdump
echo "nmap"
yum -y install nmap
echo "telnet"
yum -y install telnet

# Install locate
echo "Installing locate"
yum -y install mlocate



