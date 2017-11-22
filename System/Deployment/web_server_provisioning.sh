# Install Django
echo "Install Django"
pip3.6 install Django


mkdir -p /home/vagrant/example_dir
chown vagrant:vagrant /home/vagrant/example_dir
echo "HELLO BACKUP" > /home/vagrant/example_dir/f.txt
chown vagrant:vagrant /home/vagrant/example_dir/f.txt

# authorize ssh key of backup machine
# FIXME: this adds the key every time we re-provision the machine. Does not 
# seem to be a problem, but still not good.
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCWse17nPQ/8bYN+W2fg37ea/++Y6T6rTkod/GN0MCb/LnEWhZe+H/Q/u3JDelH8/EFvQI/MmjTSKA+ttJn7hWiaLZ44p5N6sPtFul9V5saeMM6iVywrFGNFRhPeASF5PYvBmTQpOwgN4yLdfWY3AgTzxYF1uXQrztQ/eu3LOWiwINRqQG/rwieWgC6Bs+v0EcNLJ3r7YY70SHIcsIEBiQkHlxI75M79e2UCnLU5PTRn/GsGh/s1JeUEZi9yUbSMfzjnjAMuyg04SdFulAgFSXb0hCcXM8qTO4yUKEsPLRcNROywpkfH0fSVq3/pKRKZpjdYoVFGwC18la85xGatvbx vagrant@ict-networks-010-000-002-015.fwd-v4.ethz.ch" >> /home/vagrant/.ssh/authorized_keys