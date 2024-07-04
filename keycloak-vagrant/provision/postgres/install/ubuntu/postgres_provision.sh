#!/bin/bash
source /vagrant/environment.properties

echo -e "${ORANGE}PostgreSQL provisioning ${YELLOW}START${ORANGE}!!!${NC}"

sudo bash /vagrant/provision/postgres/install/ubuntu/pgserver_provision.sh

echo -e "${ORANGE}PostgreSQL provisioning ${GREEN}DONE${ORANGE}!!!${NC}"