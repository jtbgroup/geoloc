#!/usr/bin/env bash
# =============================================================================
# fetch-locations.sh — Downloads and formats Airport and Port data
# =============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_DIR="$SCRIPT_DIR/demo-data"
mkdir -p "$OUTPUT_DIR"

# ─── Airports (Source: OurAirports) ──────────────────────────────────────────
echo "▶ Processing Airports..."
# Added 'gsub("\"";"")' to clean quotes and 'tonumber?' to avoid crashes
curl -sL "https://davidmegginson.github.io/ourairports-data/airports.csv" | jq -Rn '
  (input | split(",")) as $headers |
  [inputs | split(",") | 
  select(.[4] != "" and .[5] != "") | # Ensure coordinates exist
  {
    name: .[3] | gsub("\""; ""),
    featureClass: "S",
    featureCode: "AIRP",
    sourceId: "OURAIRPORTS",
    geom: { 
      type: "Point", 
      coordinates: [(.[5] | tonumber?), (.[4] | tonumber?)] 
    },
    properties: { icao: .[1] | gsub("\""; ""), iata: .[13] | gsub("\""; "") }
  }] | map(select(.geom.coordinates[0] != null))' > "$OUTPUT_DIR/geoloc_02_airports.json"

# ─── Ports (Source: UN/LOCODE) ──────────────────────────────────────────────
echo "▶ Processing Ports..."
# Similar cleanup for the Ports CSV
curl -sL "https://raw.githubusercontent.com/datasets/un-locode/master/data/locode.csv" | jq -Rn '
  (input | split(",")) as $headers |
  [inputs | split(",") | 
  select(.[10] != "" and .[11] != "") | # Ensure coordinates exist
  {
    name: .[3] | gsub("\""; ""),
    featureClass: "S",
    featureCode: "PORT",
    sourceId: "UN_LOCODE",
    geom: { 
      type: "Point", 
      coordinates: [(.[11] | tonumber?), (.[10] | tonumber?)] 
    },
    properties: { locode: (.[1] + .[2]), country: .[0] }
  }] | map(select(.geom.coordinates[0] != null))' > "$OUTPUT_DIR/geoloc_03_ports.json"

echo "✔ Successfully generated airport and port files in $OUTPUT_DIR"