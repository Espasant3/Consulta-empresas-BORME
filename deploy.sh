#!/bin/bash

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

check_dependencies() {
    log_info "Verificando dependencias..."
    command -v docker &>/dev/null || { log_error "Docker no está instalado"; exit 1; }
    docker compose version &>/dev/null || { log_error "Docker Compose V2 no está disponible"; exit 1; }
    log_info "✓ Dependencias verificadas"
}

cleanup() {
    log_info "Limpiando despliegue anterior..."
    docker compose down --remove-orphans  > /dev/null 2>&1 || true
}

deploy() {
    log_info "Construyendo e iniciando servicios..."
    docker compose up -d --build  > /dev/null
    log_info "✓ Servicios desplegados en segundo plano"
}

# Esperar a que un servicio esté healthy usando docker compose ps
wait_for_healthy() {
    local service=$1
    local max_attempts=30
    local attempt=1

    log_info "Esperando a que '$service' esté listo..."
    while [ $attempt -le $max_attempts ]; do
        if docker compose ps --format 'table {{.Names}}\t{{.Status}}\t{{.Health}}' | grep -E "^$service\s" | grep -q "healthy"; then
            log_info "✓ $service listo"
            return 0
        fi
        sleep 2
        ((attempt++))
    done
    log_warn "⚠️  '$service' tardó en estar listo, pero continuamos..."
}

show_info() {
    log_info "=== DESPLIEGUE COMPLETADO ==="
    echo ""
    echo "   🌐 Aplicación: http://localhost"
    echo ""
    echo "💡 La base de datos está vacía inicialmente."
    echo "   La primera consulta descargará y procesará los datos automáticamente."
    echo ""
    echo "🛠️  Comandos útiles:"
    echo "   - Ver logs:          docker compose logs -f"
    echo "   - Ver estado:        docker compose ps"
    echo "   - Detener servicios: docker compose down"
    echo ""
    log_info "¡Sistema listo para usar! 🎉"
}

main() {
    log_info "Iniciando despliegue de BORME API..."
    check_dependencies
    cleanup
    deploy
    wait_for_healthy "borme-database"
    wait_for_healthy "borme-backend"
    wait_for_healthy "borme-frontend"
    show_info
}

main "$@"
