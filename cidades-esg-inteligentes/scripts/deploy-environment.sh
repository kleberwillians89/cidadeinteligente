#!/usr/bin/env sh
set -eu

cd "$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

ENVIRONMENT="${1:?Uso: ./scripts/deploy-environment.sh staging|production}"

case "$ENVIRONMENT" in
  staging)
    PORT="8081"
    OVERRIDE_FILE="docker-compose.staging.yml"
    ;;
  production)
    PORT="8082"
    OVERRIDE_FILE="docker-compose.production.yml"
    ;;
  *)
    echo "Ambiente inválido: ${ENVIRONMENT}"
    exit 2
    ;;
esac

export APP_PORT="$PORT"
docker compose -p "cidades-esg-${ENVIRONMENT}" \
  -f docker-compose.yml -f "$OVERRIDE_FILE" up -d --no-build --wait --wait-timeout 180

sh ./scripts/smoke-test.sh "http://localhost:${PORT}"
docker compose -p "cidades-esg-${ENVIRONMENT}" \
  -f docker-compose.yml -f "$OVERRIDE_FILE" ps
