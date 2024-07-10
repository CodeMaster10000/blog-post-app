#!/bin/bash

# This script checks if Keycloak is ready by making an HTTP request
# Modify the endpoint according to your Keycloak setup

until curl -s "http://keycloak:${KEYCLOAK_PORT}/auth/realms/${KEYCLOAK_REALM}" > /dev/null; do
  echo "Waiting for Keycloak to be ready..."
  sleep 5
done

echo "Keycloak is ready. Starting blog-api..."
exec "$@"