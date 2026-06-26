#!/bin/bash
# Quick login test
LOGIN=$1
PASS=$2
URL="${3:-http://192.168.100.14:8080}"
API="${URL}/api/v1/mobile/auth/login"

echo "Testing login: $LOGIN"
echo "URL: $API"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API" \
    -H "Content-Type: application/json" \
    -d "{\"login\": \"${LOGIN}\", \"motDePasse\": \"${PASS}\"}")
HTTP_CODE=$(echo "$RESPONSE" | tail -1)
BODY=$(echo "$RESPONSE" | head -n -1)
echo "HTTP: $HTTP_CODE"
echo "Body: $BODY"
