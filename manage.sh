#!/bin/bash

set -e

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

show_usage() {
    echo "Uso: $0 [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  start     - Desplegar servicios (docker compose up -d)"
    echo "  stop      - Detener servicios (docker compose down)"
    echo "  stopAll   - Detener servicios y eliminar volúmenes (docker compose down -v --remove-orphans)"
    echo "  restart   - Reiniciar servicios (docker compose restart)"
    echo "  logs      - Mostrar logs (docker compose logs -f)"
    echo "  status    - Estado de los servicios (docker compose ps)"
    echo "  build     - Reconstruir imágenes (docker compose build --no-cache)"
    echo "  cleanup   - Limpiar todo (docker compose down -v --remove-orphans)"
    echo "  help      - Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 start      # Desplegar la aplicación"
    echo "  $0 logs       # Ver logs en tiempo real"
    echo "  $0 stop       # Detener la aplicación"
    echo "  $0 stopAll    # Detener y borrar volúmenes"
}

case "$1" in
    start)
        log_info "Iniciando servicios..."
        docker compose up -d
        log_info "Servicios iniciados. Usa '$0 logs' para ver los logs."
        ;;
    stop)
        log_info "Deteniendo servicios..."
        docker compose down
        ;;
    stopAll)
        log_warn "Deteniendo servicios y eliminando volúmenes..."
        docker compose down -v --remove-orphans
        log_info "Servicios y volúmenes eliminados correctamente."
        ;;
    restart)
        log_info "Reiniciando servicios..."
        docker compose restart
        ;;
    logs)
        docker compose logs -f
        ;;
    status)
        echo -e "${BLUE}=== ESTADO DE SERVICIOS ===${NC}"
        docker compose ps
        echo -e "${BLUE}=== VOLÚMENES ===${NC}"
        docker volume ls | grep borme
        ;;
    build)
        log_info "Reconstruyendo imágenes..."
        docker compose build --no-cache
        ;;
    cleanup)
        log_warn "⚠ Esto eliminará TODOS los datos y volúmenes!"
        read -p "¿Estás seguro? (s/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Ss]$ ]]; then
            log_info "Limpiando todo..."
            docker compose down -v --remove-orphans
            log_info "Limpieza completada"
        else
            log_info "Operación cancelada"
        fi
        ;;
    help|--help|-h)
        show_usage
        ;;
    *)
        if [ -z "$1" ]; then
            show_usage
        else
            echo -e "${RED}Error: Comando '$1' no reconocido${NC}"
            echo ""
            show_usage
            exit 1
        fi
        ;;
esac
