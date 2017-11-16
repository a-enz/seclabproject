Check machine_setup_doc.txt for the documentation of the setup
already done on the base machine image.

Further setup mostly involves VBox specific tasks.

------------------
DOWNLOAD RESOURCES
------------------
Check that you have the .ova files for the 'base_machine' and
'firewall':
base_machine: <link>
firewall: <link>

---------------------
VBOX SETUP
---------------------
'Adapter' will refer to VBox network adapters 
(Setting > Network > AdapterX). The network names don't need 
to be the same as the once used below, but the order of the 
adapters is important (for the firewall)

We will set up the following topology in VBox:

[INET] ----- I0 [FW] I2 ---- [INTERNAL]
{client}         I1       {backup, core ca}
                 |
                 |
               [DMZ]
            {web_server}

INET: 192.168.70.0/24 subnet
DMZ: 192.168.51.0/24 subnet
INET: 192.168.50.0/24 subnet

Firewall 
========
Login:  root (pw: pfsense), admin: (pw: pfsense)

Import applience, create 3 network adapters:
- Adapter1: Host-only 'vboxnet0'
  -> In Preferences > Network > Host-only Networks
     configure 'vboxnet0' to be have an IPv4 address in 192.168.70.0/24, IPv6 does not matter
- Adapter2: Internal 'imovies_internal'
- Adapter3: Internal 'imovies_dmz'

IPs are already configured, but just for clarity:
- Adapter1: 192.168.70.10
- Adapter2: 192.168.50.50
- Adapter3: 192.168.51.51


Base Machine
============
Login:  root (pw: secure), iadmin: (pw: secure)

Import appliance, create clones and name them 
(backup, web_server, core_ca)
- backup:
  Adapter1: Internal 'imovies_internal'

  Start the machine, login as root(pw: secure), run set_ip.sh
  GATEWAY=192.168.50.50
  IPADDR=192.168.50.32
  NETWORK=192.168.50.0

- web_server:
  Adapter1: Internal 'imovies_dmz'
  
  Start the machine, login as root(pw: secure), run set_ip.sh
  GATEWAY=192.168.51.51
  IPADDR=192.168.51.14
  NETWORK=192.168.51.0

- core_ca:
  Adapter1: Internal 'imovies_internal'
  
  Start the machine, login as root(pw: secure), run set_ip.sh
  GATEWAY=192.168.50.50
  IPADDR=192.168.50.31
  NETWORK=192.168.50.0



---------------------
HOST INFO (CLIENT)
---------------------
Your host machine should now be able to connect through the
Adapter1 interface (192.168.70.10) to NAT'ed ports. Try pinging the interface from your host.

SSH ports
=========
It should be possible to ssh to any machine in the network.
ssh -p <port> root@192.168.70.10 
<port> second part of IP: e.g. 192.168.51.14 -> 5114

Further ports
=============
port 80 and 443 are mapped to web server

... more details coming:
- setting up an ssh tunnel to the web dashboard of firewall

