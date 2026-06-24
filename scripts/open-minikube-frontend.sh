#!/usr/bin/env bash
set -euo pipefail

PORT="${1:-30080}"

if ! kubectl get svc frontend -n motus >/dev/null 2>&1; then
  echo "Erreur : le service frontend n'existe pas dans le namespace motus."
  echo "Lancez d'abord : ./scripts/deploy-minikube.sh"
  exit 1
fi

echo "=== Accès au frontend Motus ==="
echo ""
echo "Sur macOS avec le driver Docker, l'URL http://\$(minikube ip):30080"
echo "ne fonctionne pas directement dans le navigateur."
echo ""
echo "Ce script crée un tunnel local. Ouvrez :"
echo ""
echo "  http://localhost:${PORT}"
echo ""
echo "Laissez ce terminal ouvert (Ctrl+C pour arrêter le tunnel)."
echo ""

kubectl port-forward -n motus "service/frontend" "${PORT}:80"
