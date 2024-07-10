#!/bin/bash


# Perform the import
bash /opt/keycloak/bin/kc.sh import --file /opt/keycloak/data/import-data.json

sleep 10

exec /opt/keycloak/bin/kc.sh start-dev --http-port="$KC_HTTP_PORT"