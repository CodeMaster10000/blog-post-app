#!/bin/bash
source /vagrant/environment.properties

echo -e "${PURPLE}PostgreSQL Server provisioning ${YELLOW}START${PURPLE}!!!${NC}"

sudo apt install -y postgresql-14
sudo systemctl enable postgresql
sudo systemctl start postgresql

#Vagrant user no pwd prompt
sudo bash -c "echo 'vagrant ALL=(ALL) NOPASSWD: ALL' >> /etc/sudoers"

sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';"

# Keycloak
sudo -u postgres psql <<EOF
CREATE DATABASE $keycloak_db_name;
CREATE USER $keycloak_db_username WITH ENCRYPTED PASSWORD '$keycloak_db_password';
GRANT ALL PRIVILEGES ON DATABASE $keycloak_db_name TO $keycloak_db_username;
GRANT ALL ON SCHEMA public TO $keycloak_db_username;
EOF


cd /home/vagrant

# Configure locale
sudo cp /vagrant/provision/postgres/install/ubuntu/locale.gen /etc
sudo locale-gen en_US.UTF-8
sudo update-locale LANG=en_US.UTF-8

sudo cp /vagrant/provision/postgres/install/ubuntu/pg_hba.conf /etc/postgresql/14/main/
sudo bash /vagrant/provision/postgres/install/ubuntu/postgres-parameterized.sh

# Reload and restart PostgreSQL
sudo systemctl reload postgresql
sudo systemctl restart postgresql

echo -e "${PURPLE}PostgreSQL Server provisioning ${GREEN}DONE${PURPLE}!!!${NC}"