#!/bin/bash

LOG_FILE="/var/lib/mysql/init.log"

echo "$(date) - Starting initialization script." | tee -a $LOG_FILE

# Debug: Print environment variables
echo "MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}" | tee -a $LOG_FILE
echo "MYSQL_DATABASE: ${MYSQL_DATABASE}" | tee -a $LOG_FILE
echo "KEYCLOAK_DATABASE: ${KEYCLOAK_DATABASE}" | tee -a $LOG_FILE

# Wait for the MySQL server to be ready
while ! mysqladmin ping -h localhost --silent; do
  sleep 1
done

echo "$(date) - MySQL server is up and running." | tee -a $LOG_FILE

# Execute the MySQL commands
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
  CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
  GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
  FLUSH PRIVILEGES;
  CREATE DATABASE IF NOT EXISTS \`${MYSQL_DATABASE}\`;
  CREATE DATABASE IF NOT EXISTS \`${KEYCLOAK_DATABASE}\`;
EOSQL

echo "$(date) - Permissions granted and databases created. Initialization script completed." | tee -a $LOG_FILE

# Log the entire content of the log file
echo "===== Initialization Log Content =====" | tee -a $LOG_FILE
cat $LOG_FILE
