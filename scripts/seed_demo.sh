#!/usr/bin/env bash
# =============================================================================
# seed-demo.sh — Injects users and geographic data via the REST API
# =============================================================================

set -euo pipefail

BASE_URL="${1:-http://localhost:8081}"
ADMIN_USER="${2:-admin}"
ADMIN_PASS="${3:-admin123}"
LIMIT="${4:-1000}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DATA_DIR="$SCRIPT_DIR/demo-data"
USERS_FILE="$DATA_DIR/users.json"

COOKIE_JAR=$(mktemp)
trap 'rm -f "$COOKIE_JAR" /tmp/seed_response.json' EXIT

# ─── Colors ──────────────────────────────────────────────────────────────────
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; CYAN='\033[0;36m'; NC='\033[0m'
log_info()    { echo -e "  ${CYAN}ℹ${NC}  $*"; }
log_ok()      { echo -e "  ${GREEN}✔${NC}  $*"; }
log_warn()    { echo -e "  ${YELLOW}⚠${NC}  $*"; }
log_section() { echo -e "\n${CYAN}▶ $*${NC}"; }

# ─── Admin Login ─────────────────────────────────────────────────────────────
log_section "Authenticating as '$ADMIN_USER'"
curl -s -o /dev/null -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}" \
  -c "$COOKIE_JAR"

# ─── Seed Users ──────────────────────────────────────────────────────────────
log_section "Seeding users"
if [ -f "$USERS_FILE" ]; then
  while read -r USER; do
    USERNAME=$(echo "$USER" | jq -r '.username')
    STATUS=$(curl -s -o /tmp/seed_response.json -w "%{http_code}" \
      -X POST "$BASE_URL/api/users" -H "Content-Type: application/json" \
      -b "$COOKIE_JAR" -d "$USER")
    [[ "$STATUS" =~ ^20[01]$ ]] && log_ok "User: $USERNAME" || log_warn "User $USERNAME: $STATUS"
  done < <(jq -c '.[]' "$USERS_FILE")
fi


# ─── Seed Geo Data ──────────────────────────────────────────────────────────
log_section "Seeding geographic locations"
# 1. Ouverture de la boucle for
for FILE in "$DATA_DIR"/geoloc_*.json; do
  log_info "Processing $(basename "$FILE")..."
  
  # 2. Ouverture de la boucle while
  jq -c ".[] | limit($LIMIT; .)" "$FILE" | while read -r LOCATION; do
    
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" -b "$COOKIE_JAR" \
      -X POST "$BASE_URL/api/geo-features" \
      -H "Content-Type: application/json" \
      --data-binary "$LOCATION")

    # 3. Ouverture du if
    if [[ ! "$STATUS" =~ ^20[01]$ ]]; then
       log_info "  Skipped/Failed ($STATUS): $(echo "$LOCATION" | jq -r '.name')"
    fi
    # 3. Fermeture du if
  done
  # 2. Fermeture du while
  
  log_ok "Finished $(basename "$FILE")"
done

# ─── Logout ──────────────────────────────────────────────────────────────────
curl -s -o /dev/null -X POST "$BASE_URL/api/auth/logout" -b "$COOKIE_JAR"
log_section "Done. All demo data injected."