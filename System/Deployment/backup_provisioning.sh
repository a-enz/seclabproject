# Provision backup as root


# create the backup user and group if not already there
username=$1
pass=$(perl -e 'print crypt($ARGV[0], "password")' $2)

echo "Setting up user and group for ${username}"
# Check if group already exists before creating
grep -q "$username" /etc/group
if [ $? -ne 0 ]; then #not equal
	echo "Creating new group $username"
	groupadd $username
else
	echo "Group $username already exists"
fi  

# Check if user already exists before creating
grep -q "$username" /etc/passwd
if [ $? -ne 0 ]; then #not equal
	echo "Creating new user $username"
	useradd -p "$pass" -d /home/"$username" \
	-m -g "$username" "$username"
else
	echo "User $username already exists"
fi 



