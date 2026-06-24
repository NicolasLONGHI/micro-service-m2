#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "=== Déploiement Motus sur Minikube ==="

if ! command -v minikube >/dev/null 2>&1; then
  echo "Erreur : minikube n'est pas installé."
  echo "Installez-le : https://minikube.sigs.k8s.io/docs/start/"
  exit 1
fi

if ! command -v kubectl >/dev/null 2>&1; then
  echo "Erreur : kubectl n'est pas installé."
  exit 1
fi

if ! minikube status >/dev/null 2>&1; then
  echo "Démarrage de Minikube..."
  minikube start --cpus=4 --memory=6144
fi

echo "Configuration du daemon Docker de Minikube..."
eval "$(minikube docker-env)"

echo "Construction des images Docker..."
docker build -t motus-player-service:latest ./backend/player-service
docker build -t motus-game-service:latest ./backend/game-service
docker build -t motus-score-service:latest ./backend/score-service
docker build -t motus-frontend:latest -f k8s/frontend/Dockerfile .
docker build -t motus-dictionary-loader:latest -f k8s/dictionary-loader/Dockerfile .

echo "Application des manifests Kubernetes..."
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/player-db.yaml
kubectl apply -f k8s/game-db.yaml
kubectl apply -f k8s/score-db.yaml
kubectl apply -f k8s/player-service.yaml
kubectl apply -f k8s/game-service.yaml
kubectl apply -f k8s/score-service.yaml
kubectl apply -f k8s/frontend.yaml

echo "Attente des déploiements..."
kubectl rollout status deployment/player-db -n motus --timeout=180s
kubectl rollout status deployment/game-db -n motus --timeout=180s
kubectl rollout status deployment/score-db -n motus --timeout=180s
kubectl rollout status deployment/player-service -n motus --timeout=300s
kubectl rollout status deployment/game-service -n motus --timeout=300s
kubectl rollout status deployment/score-service -n motus --timeout=300s
kubectl rollout status deployment/frontend -n motus --timeout=180s

echo "Lancement du chargement du dictionnaire..."
kubectl delete job dictionary-loader -n motus --ignore-not-found
kubectl apply -f k8s/dictionary-loader/job.yaml
kubectl wait --for=condition=complete job/dictionary-loader -n motus --timeout=600s

MINIKUBE_IP="$(minikube ip)"
echo ""
echo "=== Déploiement terminé ==="
echo ""
if minikube profile list 2>/dev/null | grep -q "docker.*OK"; then
  echo "Accès au frontend (macOS / driver Docker) :"
  echo "  1. Dans un NOUVEAU terminal :"
  echo "       ./scripts/open-minikube-frontend.sh"
  echo "  2. Ouvrez : http://localhost:30080"
  echo ""
  echo "  (L'URL http://${MINIKUBE_IP}:30080 ne fonctionne pas directement sur Mac.)"
else
  echo "Frontend : http://${MINIKUBE_IP}:30080"
fi
echo ""
echo "Commandes utiles :"
echo "  ./scripts/open-minikube-frontend.sh   # tunnel navigateur (Mac)"
echo "  kubectl get pods -n motus"
echo "  kubectl logs -f deployment/game-service -n motus"
echo "  ./scripts/cleanup-minikube.sh"
