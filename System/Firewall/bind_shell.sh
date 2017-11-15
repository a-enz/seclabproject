# Binding a shell to a port using netcat.
# Because FreeBSD does not support -e or -c to bind executable
# we use a workaround described here (client/server model):
# https://manned.org/nc.openbsd/6f0a5cf9
rm -f /tmp/f; mkfifo /tmp/f
cat /tmp/f | /bin/sh -i 2>&1 | nc -k -l 1234 > /tmp/f