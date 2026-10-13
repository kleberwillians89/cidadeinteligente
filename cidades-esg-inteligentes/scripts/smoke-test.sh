#!/usr/bin/env sh
set -eu

BASE_URL="${1:-http://localhost:8080}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-30}"
ATTEMPT=1

echo "Aguardando a API em ${BASE_URL}..."
while [ "$ATTEMPT" -le "$MAX_ATTEMPTS" ]; do
  if curl --fail --silent "${BASE_URL}/actuator/health/readiness" >/dev/null; then
    echo "Health check aprovado na tentativa ${ATTEMPT}."
    break
  fi
  if [ "$ATTEMPT" -eq "$MAX_ATTEMPTS" ]; then
    echo "A API não ficou saudável após ${MAX_ATTEMPTS} tentativas."
    exit 1
  fi
  ATTEMPT=$((ATTEMPT + 1))
  sleep 2
done

curl --fail --silent "${BASE_URL}/api/cities" | tee /tmp/cities-response.json
echo
echo "Smoke test concluído com sucesso."
