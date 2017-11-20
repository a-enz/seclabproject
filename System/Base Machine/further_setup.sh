# Further Sstup of the base machine
# Instead of providing a new .ova image each time 
# something is changed on the base machine, 
# this script provides a more lightweight way to
# update the base machine setup$

## Disable firewalld
echo "Stopping firewalld"
systemctl stop firewalld
systemctl disable firewalld

## Set approx. correct time
echo "Setting approx. correct time"
timedatectl set-timezone 'Europe/Zurich'
echo "Provide the time in format HH:MM:SS"
read TIME
timedatectl set-time $TIME
echo "This is the time you have set:"
date