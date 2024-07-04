#!/bin/bash

# Read the PID from the file
pid=$(cat /opt/keycloak/keycloak.pid)

# Check if the process is running
if ps -p $pid > /dev/null; then
    # Process is running, so kill it
    kill $pid
    wait $pid
    echo "Process Keycloak with PID $pid has been terminated."
    exit 0
else
    echo "Process Keycloak with PID $pid is not running."
    exit 0
fi
