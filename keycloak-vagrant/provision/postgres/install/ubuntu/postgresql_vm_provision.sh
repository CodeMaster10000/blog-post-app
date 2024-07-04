#!/bin/bash
source /vagrant/environment.properties

echo -e "${BLUE}PostgreSQL VM provisioning ${YELLOW}START${BLUE}!!!${NC}"

sudo bash /vagrant/provision/postgres/install/ubuntu/postgres_provision.sh

echo -e "${BLUE}PostgreSQL VM provisioning ${GREEN}DONE${BLUE}!!!${NC}"
