#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "Suppression du namespace motus..."
kubectl delete namespace motus --ignore-not-found

echo "Nettoyage terminé."
