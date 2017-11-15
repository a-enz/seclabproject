# open an ssh tunnel to the network interface of the 
# firewall where the web dashboard is accessible
# still needs quite a bit of user interaction:
# 1) password login
# 2) start firefox socks proxy over port 4041
# 
PORT=4041
echo "Starting ssh tunneling to firewall web dashboard...\n"
ssh -p 5050 -D $PORT -f -N admin@192.168.70.10

echo "\n\nTUNNEL TEARDOWN"
echo "Tunnel created, please kill the tunneling process later."
echo "To find out the process id use:"
echo "  ps aux | grep ssh"
echo "and kill it with:"
echo "  kill <id>\n\n"

echo "FIREFOX SOCKS PROXY"
echo "You should now be able to start a firefox session with" \
    "a socks proxy over outgoing port $PORT and access the" \
    "web dashboard of the firewall at https://192.168.50.50"