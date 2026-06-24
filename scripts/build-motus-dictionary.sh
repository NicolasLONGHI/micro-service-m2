#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

OUTPUT="backend/game-service/dictionary/mots_motus.txt"
SOURCE_URL="${MOTUS_SOURCE_URL:-https://raw.githubusercontent.com/GDumail/autoMOTUS/main/frgut.txt}"
CACHE="/tmp/motus-frgut-source.txt"
MIN_LENGTH="${MIN_WORD_LENGTH:-6}"
MAX_LENGTH="${MAX_WORD_LENGTH:-10}"

echo "=== Construction du dictionnaire Motus (${MIN_LENGTH}-${MAX_LENGTH} lettres) ==="

if [ ! -f "$CACHE" ]; then
  echo "Téléchargement de la liste Français-GUTenberg..."
  curl -fsSL "$SOURCE_URL" -o "$CACHE"
fi

echo "Filtrage selon les règles Motus (noms communs, infinitifs, participes)..."
awk -v min_len="$MIN_LENGTH" -v max_len="$MAX_LENGTH" \
  -f scripts/filter-motus-dictionary.awk "$CACHE" \
  | cut -d',' -f1 | sort -u > "$OUTPUT"

WORD_COUNT=$(wc -l < "$OUTPUT" | tr -d ' ')
echo "=== Dictionnaire Motus généré : $WORD_COUNT mots → $OUTPUT ==="
echo "Relancez le chargement BDD (docker compose down -v game-db-data ou FORCE_DICTIONARY_RELOAD=1)."
