#!/usr/bin/env bash
# =============================================================================
# reset-sql.sh — Wipes all data except the admin user
# =============================================================================

set -euo pipefail

DB_HOST="${1:-localhost}"
DB_PORT="${2:-5432}"
DB_NAME="${3:-geoloc}"
DB_USER="${4:-geoloc}"
DB_PASS="${5:-geoloc}"
ADMIN_USER="${6:-admin}"

GREEN='\033[0;32m'; RED='\033[0;31m'; CYAN='\033[0;36m'; NC='\033[0m'
log_ok()      { echo -e "  ${GREEN}✔${NC}  $*"; }
log_error()   { echo -e "  ${RED}✘${NC}  $*"; }
log_section() { echo -e "\n${CYAN}▶ $*${NC}"; }

if ! command -v psql &> /dev/null; then
  log_error "psql is required. Install: apt install postgresql-client (Linux) or brew install libpq (macOS)"
  exit 1
fi

echo -e "${RED}"
echo "  ██████╗ ███████╗███████╗███████╗████████╗"
echo "  ██╔══██╗██╔════╝██╔════╝██╔════╝╚══██╔══╝"
echo "  ██████╔╝█████╗  ███████╗█████╗     ██║   "
echo "  ██╔══██╗██╔══╝  ╚════██║██╔══╝     ██║   "
echo "  ██║  ██║███████╗███████║███████╗   ██║   "
echo "  ╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝   ╚═╝   "
echo -e "${NC}"
echo "  This will DELETE ALL DATA on:"
echo "    Host     : $DB_HOST:$DB_PORT"
echo "    Database : $DB_NAME"
echo ""
echo "  User '$ADMIN_USER' will be kept."
echo ""
read -r -p "  Type 'yes' to confirm: " CONFIRM
if [ "$CONFIRM" != "yes" ]; then
  echo "  Aborted."
  exit 0
fi

log_section "Running reset"

export PGPASSWORD="$DB_PASS"

psql \
  --host="$DB_HOST" \
  --port="$DB_PORT" \
  --username="$DB_USER" \
  --dbname="$DB_NAME" \
  --set ON_ERROR_STOP=1 \
  <<SQL

-- ─── 1. Clean Geographic Data ──────────────────────────────────────────────
DO \$\$ BEGIN TRUNCATE TABLE geo_feature CASCADE; EXCEPTION WHEN UNDEFINED_TABLE THEN NULL; END \$\$;

-- ─── 2. Clean Other Users (keep admin) ─────────────────────────────────────
DELETE FROM app_user WHERE username != '$ADMIN_USER';

-- ─── 3. Reset ID Sequence ──────────────────────────────────────────────────
DO \$\$ BEGIN
  PERFORM setval(pg_get_serial_sequence('geo_feature', 'id'), 1, false);
EXCEPTION WHEN OTHERS THEN NULL; END \$\$;

SQL

unset PGPASSWORD

log_ok "Reset complete — Geographic data cleared, '$ADMIN_USER' preserved."
echo ""
echo "  Next step: ./scripts/seed_demo.sh"