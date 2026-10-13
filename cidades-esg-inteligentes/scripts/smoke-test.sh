#!/usr/bin/env sh
set -eu

BASE_URL="${1:-http://localhost:8080}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-30}"
case "$MAX_ATTEMPTS" in ''|*[!0-9]*|0) echo 'MAX_ATTEMPTS deve ser inteiro positivo.' >&2; exit 2;; esac
EVIDENCE_DIR="${EVIDENCE_DIR:-$(mktemp -d)}"
mkdir -p "$EVIDENCE_DIR"
ATTEMPT=1

echo "Aguardando a API em ${BASE_URL}..."
while :; do
  if curl --fail --silent --show-error --connect-timeout 3 --max-time 10 \
    "${BASE_URL}/actuator/health/readiness" -o "$EVIDENCE_DIR/readiness.json"; then
    break
  fi
  if [ "$ATTEMPT" -ge "$MAX_ATTEMPTS" ]; then
    echo "API indisponível após ${MAX_ATTEMPTS} tentativas." >&2
    exit 1
  fi
  ATTEMPT=$((ATTEMPT + 1))
  sleep 2
done

for RESOURCE in actuator/health api/cities api/cities/ranking; do
  case "$RESOURCE" in
    actuator/health) FILE=health;;
    api/cities) FILE=cities;;
    api/cities/ranking) FILE=ranking;;
  esac
  curl --fail --silent --show-error --connect-timeout 3 --max-time 10 \
    "$BASE_URL/$RESOURCE" -o "$EVIDENCE_DIR/$FILE.json"
done

python3 - "$EVIDENCE_DIR" <<'PY'
import json
import sys
from pathlib import Path
folder = Path(sys.argv[1])
for name in ('readiness', 'health'):
    data = json.loads((folder / f'{name}.json').read_text())
    if data.get('status') != 'UP':
        raise SystemExit(f'{name}: status diferente de UP')
for name in ('cities', 'ranking'):
    data = json.loads((folder / f'{name}.json').read_text())
    if not isinstance(data, list):
        raise SystemExit(f'{name}: resposta não é uma lista JSON')
    for city in data:
        if not isinstance(city, dict) or not {'id', 'name', 'state', 'esgScore'} <= city.keys():
            raise SystemExit(f'{name}: formato de cidade inválido')
# Produção sem seed pode retornar listas vazias; isso é válido.
print('Health checks e endpoints de cidades/ranking aprovados.')
PY

echo "Smoke test concluído com sucesso. Evidências: $EVIDENCE_DIR"
