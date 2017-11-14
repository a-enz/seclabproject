# Stop postfix smtp service at port 25
# (check with 'nmap localhost' or 'ss -tlpn')
postfix stop 
yum -y remove postfix