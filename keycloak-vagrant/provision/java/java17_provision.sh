#!/bin/bash
source /vagrant/environment.properties

echo -e "${ORANGE}Java17 provisioning ${YELLOW}START${ORANGE}!!!${NC}"

sudo apt install openjdk-17-jdk -y
sudo cp /vagrant/provision/java/java17.sh /etc/profile.d/

echo -e "${ORANGE}Java17 provisioning ${GREEN}DONE${ORANGE}!!!${NC}"