# Further Sstup of the base machine
# Instead of providing a new .ova image each time 
# something is changed on the base machine, 
# this script provides a more lightweight way to
# update the base machine setup$

## Disable firewalld
systemctl stop firewalld
systemctl disable firewalld