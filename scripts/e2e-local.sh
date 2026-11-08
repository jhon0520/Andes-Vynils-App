#!/usr/bin/env bash
#
# Corre la app contra un BackVynils local:
#   1. Revisa si el backend responde en localhost:3000; si no, lo levanta con Docker (Colima).
#   2. Apunta la app a la IP de este computador (api.base.url en local.properties).
#   3. Compila el APK debug y lo copia en distributions/.
#   4. Corre las pruebas E2E: Compose (connectedDebugAndroidTest) y Kraken.
#
# Uso:
#   ./scripts/e2e-local.sh                       # todo
#   ./scripts/e2e-local.sh --skip-tests          # solo backend + APK
#   ./scripts/e2e-local.sh --skip-kraken         # sin Kraken
#   ./scripts/e2e-local.sh --skip-compose        # sin pruebas de Compose
#   ./scripts/e2e-local.sh --backend-dir=RUTA    # otra carpeta de BackVynils
#   ./scripts/e2e-local.sh --release             # APK para el release en main (ver abajo)
#
# --release: quita api.base.url de local.properties para volver al dominio por defecto
# (Render, en gradle.properties), compila el APK y lo copia en distributions/.
# No toca el backend local ni corre pruebas.
#
# Variables opcionales:
#   BACKEND_DIR  ruta de BackVynils (también con --backend-dir)
#   HOST_IP      IP a usar si la detección automática no sirve (por ejemplo 192.168.1.20)

set -euo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Ruta de ejemplo: BackVynils clonado al lado de este repo (carpeta-padre/BackVynils).
# Cámbiala aquí, o usa BACKEND_DIR=/tu/ruta o --backend-dir=/tu/ruta.
BACKEND_DIR="${BACKEND_DIR:-${APP_DIR}/../BackVynils}"

BACKEND_PORT=3000
LOCAL_URL="http://localhost:${BACKEND_PORT}"
WAIT_SECONDS=300
LOCAL_PROPS="${APP_DIR}/local.properties"

RUN_COMPOSE=true
RUN_KRAKEN=true
RELEASE=false
for arg in "$@"; do
  case "$arg" in
    --skip-tests)    RUN_COMPOSE=false; RUN_KRAKEN=false ;;
    --skip-kraken)   RUN_KRAKEN=false ;;
    --skip-compose)  RUN_COMPOSE=false ;;
    --backend-dir=*) BACKEND_DIR="${arg#*=}" ;;
    --release)       RELEASE=true ;;
    -h|--help)       sed -n '2,23p' "$0"; exit 0 ;;
    *) echo "Opción desconocida: $arg (usa --help)"; exit 1 ;;
  esac
done

step() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }
ok()   { printf '\033[0;32m✔ %s\033[0m\n' "$*"; }
warn() { printf '\033[0;33m! %s\033[0m\n' "$*"; }
fail() { printf '\033[0;31m✘ %s\033[0m\n' "$*" >&2; exit 1; }

backend_up() {
  [[ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 "${LOCAL_URL}/albums" || true)" == "200" ]]
}

build_apk() {
  cd "$APP_DIR"
  ./gradlew assembleDebug
  mkdir -p distributions
  cp app/build/outputs/apk/debug/app-debug.apk distributions/app-debug.apk
  ok "APK copiado en distributions/app-debug.apk"
}

# ---------------------------------------------------------------------------
# --release: APK con el dominio por defecto (Render), sin backend local ni pruebas
# ---------------------------------------------------------------------------
if $RELEASE; then
  step "Release: dominio por defecto"
  if [[ -f "$LOCAL_PROPS" ]] && grep -q '^api\.base\.url=' "$LOCAL_PROPS"; then
    sed -i '' '/^api\.base\.url=/d' "$LOCAL_PROPS"
    ok "Quité api.base.url de local.properties"
  else
    ok "local.properties no sobrescribe api.base.url"
  fi
  ok "Dominio: $(grep '^api\.base\.url=' "${APP_DIR}/gradle.properties" | cut -d= -f2-)"

  step "Compilar APK debug"
  build_apk
  exit 0
fi

# ---------------------------------------------------------------------------
# 1. Backend
# ---------------------------------------------------------------------------
step "1/4 Backend en ${LOCAL_URL}"

if backend_up; then
  ok "El backend ya está corriendo"
else
  warn "El backend no responde; lo levanto desde ${BACKEND_DIR}"
  [[ -f "${BACKEND_DIR}/docker-compose.yml" ]] || fail "No encuentro docker-compose.yml en ${BACKEND_DIR}"

  if ! docker info >/dev/null 2>&1; then
    command -v colima >/dev/null || fail "Docker no está activo y Colima no está instalado (ver ${BACKEND_DIR}/README.md)"
    echo "Iniciando Colima…"
    colima start --cpu 2 --memory 4 --disk 30
    docker context use colima >/dev/null
  fi

  (cd "$BACKEND_DIR" && docker compose up -d --build)

  echo -n "Esperando a que la API responda (máx. ${WAIT_SECONDS}s)"
  for ((i = 0; i < WAIT_SECONDS; i += 3)); do
    backend_up && break
    echo -n "."
    sleep 3
  done
  echo
  backend_up || fail "La API no respondió. Revisa: (cd ${BACKEND_DIR} && docker compose logs web)"
  ok "Backend arriba"
fi

# Las E2E con backend real esperan los datos de ejemplo (Buscando América, Queen, Jaime Monsalve…).
if [[ "$(curl -s --max-time 5 "${LOCAL_URL}/albums")" == "[]" ]]; then
  warn "La base está vacía; cargo los datos de ejemplo (sql/data.sql)"
  (cd "$BACKEND_DIR" && docker compose exec -T db psql -q -U postgres -d vinyls < sql/data.sql)
  ok "Datos de ejemplo cargados"
fi

# ---------------------------------------------------------------------------
# 2. Dominio de la API = IP de este computador
# ---------------------------------------------------------------------------
step "2/4 Dominio de la API"

HOST_IP="${HOST_IP:-$(ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || true)}"
[[ -n "$HOST_IP" ]] || fail "No pude detectar la IP. Ejecuta: HOST_IP=192.168.x.x $0"
API_URL="http://${HOST_IP}:${BACKEND_PORT}/"

touch "$LOCAL_PROPS"
if grep -q '^api\.base\.url=' "$LOCAL_PROPS"; then
  sed -i '' "s#^api\.base\.url=.*#api.base.url=${API_URL}#" "$LOCAL_PROPS"
else
  printf '\napi.base.url=%s\n' "$API_URL" >> "$LOCAL_PROPS"
fi
ok "api.base.url=${API_URL} (en local.properties, no se sube a git)"

if [[ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 "${API_URL}albums" || true)" == "200" ]]; then
  ok "La API responde también por ${API_URL}"
else
  warn "La API no responde por ${API_URL}; revisa el firewall de macOS. El teléfono debe estar en la misma red Wi-Fi."
fi

# ---------------------------------------------------------------------------
# 3. APK
# ---------------------------------------------------------------------------
step "3/4 Compilar APK debug"

build_apk

# ---------------------------------------------------------------------------
# 4. Pruebas E2E
# ---------------------------------------------------------------------------
step "4/4 Pruebas E2E"

if ! $RUN_COMPOSE && ! $RUN_KRAKEN; then
  ok "Pruebas omitidas (--skip-tests)"
  exit 0
fi

DEVICES="$(adb devices | awk 'NR > 1 && $2 == "device" { print $1 }')"
[[ -n "$DEVICES" ]] || fail "No hay dispositivo ni emulador conectado (adb devices)"
ok "Dispositivo(s): $(echo "$DEVICES" | tr '\n' ' ')"

COMPOSE_STATUS=0
KRAKEN_STATUS=0

if $RUN_COMPOSE; then
  echo "Pruebas de Compose (Content + E2E)…"
  # Mantiene la pantalla encendida mientras corren: con la pantalla apagada no hay UI que probar.
  adb shell svc power stayon usb || true
  ./gradlew connectedDebugAndroidTest || COMPOSE_STATUS=$?
  adb shell svc power stayon false || true
  echo "Reporte: app/build/reports/androidTests/connected/debug/index.html"
fi

if $RUN_KRAKEN; then
  echo "Escenarios Kraken…"
  # connectedDebugAndroidTest desinstala la app al terminar; Kraken instala distributions/app-debug.apk.
  (cd e2e/kraken && npx kraken-node run) || KRAKEN_STATUS=$?
  echo "Reporte: e2e/kraken/reports/"
fi

echo
$RUN_COMPOSE && { [[ $COMPOSE_STATUS -eq 0 ]] && ok "Compose: en verde" || warn "Compose: con fallos"; }
$RUN_KRAKEN  && { [[ $KRAKEN_STATUS  -eq 0 ]] && ok "Kraken: en verde"  || warn "Kraken: con fallos"; }
[[ $COMPOSE_STATUS -eq 0 && $KRAKEN_STATUS -eq 0 ]]
