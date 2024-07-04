#!/bin/bash
source /vagrant/environment.properties

cat <<EOF > /opt/keycloak/keycloak.env
KEYCLOAK_ADMIN=$keycloak_admin
KEYCLOAK_ADMIN_PASSWORD=$keycloak_admin_pass
EOF

cat <<EOF > /opt/keycloak/conf/keycloak.conf
db=postgres
db-username=$keycloak_db_username
db-password=$keycloak_db_password
db-url=jdbc:postgresql://$postgresql_host:$postgresql_port/$keycloak_db_name
quarkus.transaction-manager.enable-recovery=true

health-enabled=true
metrics-enabled=true
EOF

cat <<EOF > /opt/keycloak/keycloak-start.sh
#!/bin/bash

# Start the application
bash /opt/keycloak/bin/kc.sh start-dev --http-port=$keycloak_port --db=postgres --db-username=$keycloak_db_username --db-password=$keycloak_db_password --db-url=jdbc:postgresql://$postgresql_host:$postgresql_port/$keycloak_db_name

# Capture the PID of the background process
PID=$!

# Write the PID to a file
sudo echo $PID > /opt/keycloak/keycloak.pid
EOF