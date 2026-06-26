#!/bin/bash
set -euo pipefail
BASE_URL="${1:-http://localhost:8080}"
API="${BASE_URL}/api/v1/mobile"

echo "=== Quick Test ==="
echo "Backend: $BASE_URL"

# Test 1: Health check
echo ""
echo "--- Test 1: Health check ---"
HTTP=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 5 --max-time 10 "${BASE_URL}/" 2>&1 || echo "000")
echo "HTTP: $HTTP"

# Test 2: Login
echo ""
echo "--- Test 2: Login ---"
RESP=$(curl -s -w "\n%{http_code}" --connect-timeout 5 --max-time 15 -X POST "${API}/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"login": "+243812345678", "motDePasse": "Demo@12345"}' 2>&1)
echo "Raw response: $RESP"

HTTP_CODE=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | head -n -1)
echo "HTTP: $HTTP_CODE"
echo "Body: $BODY"

# Test 3: Extract token with jq
echo ""
echo "--- Test 3: jq test ---"
JQ=""
if command -v jq &>/dev/null; then JQ="jq"
elif [ -f "$HOME/bin/jq.exe" ]; then JQ="$HOME/bin/jq.exe"
elif [ -f "$HOME/AppData/Local/Microsoft/WindowsApps/jq.exe" ]; then JQ="$HOME/AppData/Local/Microsoft/WindowsApps/jq.exe"
fi
echo "JQ: ${JQ:-NOT FOUND}"

if [ -n "$JQ" ]; then
    TOKEN=$(echo "$BODY" | "$JQ" -r '.token' 2>/dev/null || echo "EXTRACT_FAILED")
    echo "Token (first 50 chars): ${TOKEN:0:50}"
fi

echo ""
echo "=== Done ==="
