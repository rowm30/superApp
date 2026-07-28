#!/bin/bash
curl -s -X POST http://localhost:8080/realms/superapp/protocol/openid-connect/token \
  -d grant_type=password -d client_id=local-test-cli \
  -d username=mayank.test -d password=test123 -d scope=openid \
  | tee ~/keycloak-tokens.json | jq -r .access_token
