#!/bin/bash
echo "+ ATTEMPTING TO TERMINATE CURRENT WRRKRR SERVER INSTANCE +"
kill -9 $(cat /home/ubuntu/wrrkrr-server-0.1/RUNNING_PID)
rm -rf  /home/ubuntu/wrrkrr-server-0.1
unzip wrrkrr-server-0.1.zip
cd wrrkrr-server-0.1
./bin/wrrkrr-server 2> /dev/null &
echo "+ ATTEMPTED APPLICATION START +"
