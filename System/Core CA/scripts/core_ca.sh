#!/bin/bash
# Core CA webserver

case $1 in
    start)
        /bin/bash ~/start_core_ca.sh
    ;;
    stop)
        /bin/bash ~/stop_core_ca.sh
    ;;
    restart)
        /bin/bash ~/start_core_ca.sh
        /bin/bash ~/stop_core_ca.sh
    ;;
esac
exit 0
