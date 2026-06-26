#!/bin/bash
echo "Waiting 20s for backend startup..."
for i in $(seq 1 20); do
    sleep 1
    printf "."
done
echo ""
echo "Testing login..."

API="http://192.168.100.14:8080/api/v1/mobile/auth/login"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API" \
    -H "Content-Type: application/json" \
    -d '{"login": "+243812345678", "motDePasse": "Demo@12345"}')
HTTP_CODE=$(echo "$RESPONSE" | tail -1)
BODY=$(echo "$RESPONSE" | head -n -1)
echo "HTTP: $HTTP_CODE"
echo "Body: $BODY"
