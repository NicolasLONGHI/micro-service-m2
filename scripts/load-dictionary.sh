#!/bin/sh
set -e

DICTIONARY_FILE="${DICTIONARY_FILE:-/dictionary/mots_motus.txt}"
CSV_FILE="/tmp/words_import.csv"
MIN_LENGTH="${MIN_WORD_LENGTH:-6}"
MAX_LENGTH="${MAX_WORD_LENGTH:-10}"
FORCE_RELOAD="${FORCE_DICTIONARY_RELOAD:-0}"

echo "=== Chargement du dictionnaire Motus ==="

until pg_isready -h "$PGHOST" -U "$PGUSER" -d "$PGDATABASE" >/dev/null 2>&1; do
  echo "En attente de PostgreSQL..."
  sleep 2
done

echo "PostgreSQL prêt. Vérification de la table words..."

TRIES=0
until psql -h "$PGHOST" -U "$PGUSER" -d "$PGDATABASE" -tAc "SELECT to_regclass('public.words')" | grep -q words; do
  TRIES=$((TRIES + 1))
  if [ "$TRIES" -gt 60 ]; then
    echo "Erreur: la table words n'existe pas. Démarrez game-service avant le chargement."
    exit 1
  fi
  echo "En attente de la création de la table words par game-service..."
  sleep 3
done

EXISTING=$(psql -h "$PGHOST" -U "$PGUSER" -d "$PGDATABASE" -tAc "SELECT COUNT(*) FROM words" | tr -d ' ')
if [ "$EXISTING" -gt 0 ] && [ "$FORCE_RELOAD" != "1" ]; then
  echo "Dictionnaire déjà chargé ($EXISTING mots). Aucune action nécessaire."
  echo "Pour forcer le rechargement : FORCE_DICTIONARY_RELOAD=1"
  exit 0
fi

if [ "$FORCE_RELOAD" = "1" ] && [ "$EXISTING" -gt 0 ]; then
  echo "Rechargement forcé : vidage de la table words..."
  psql -h "$PGHOST" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 -c "TRUNCATE TABLE words RESTART IDENTITY CASCADE;"
fi

if [ ! -f "$DICTIONARY_FILE" ]; then
  echo "Erreur: fichier dictionnaire introuvable ($DICTIONARY_FILE)"
  echo "Générez-le avec : ./scripts/build-motus-dictionary.sh"
  exit 1
fi

echo "Préparation du fichier CSV (Motus ${MIN_LENGTH}-${MAX_LENGTH} lettres, source: $(basename "$DICTIONARY_FILE"))..."

FILTER_AWK="/filter-motus-dictionary.awk"
if [ ! -f "$FILTER_AWK" ]; then
  FILTER_AWK="scripts/filter-motus-dictionary.awk"
fi

if [ "$(basename "$DICTIONARY_FILE")" = "mots_motus.txt" ]; then
  awk -v min_len="$MIN_LENGTH" -v max_len="$MAX_LENGTH" '
  {
      word = toupper($0)
      gsub(/[^A-Z]/, "", word)
      len = length(word)
      if (len >= min_len && len <= max_len && !seen[word]++) {
          if (len <= 6) diff = "EASY"
          else if (len <= 8) diff = "MEDIUM"
          else diff = "HARD"
          print word ",FR," diff
      }
  }' "$DICTIONARY_FILE" > "$CSV_FILE"
else
  awk -v min_len="$MIN_LENGTH" -v max_len="$MAX_LENGTH" -f "$FILTER_AWK" "$DICTIONARY_FILE" > "$CSV_FILE"
fi

WORD_COUNT=$(wc -l < "$CSV_FILE" | tr -d ' ')
echo "Import de $WORD_COUNT mots dans PostgreSQL..."

psql -h "$PGHOST" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 -c \
  "\\COPY words (word_value, language, difficulty) FROM '$CSV_FILE' WITH (FORMAT csv)"

FINAL_COUNT=$(psql -h "$PGHOST" -U "$PGUSER" -d "$PGDATABASE" -tAc "SELECT COUNT(*) FROM words" | tr -d ' ')
echo "=== Dictionnaire chargé avec succès : $FINAL_COUNT mots ==="
