#!/bin/bash

set -e  # Exit on any error

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Funciones de logging
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar dependencias
check_dependencies() {
    log_info "Verificando dependencias..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker no está instalado"
        exit 1
    fi
    
    # Verificar Docker Compose V2
    if ! docker compose version &> /dev/null; then
        log_error "Docker Compose V2 no está disponible"
        echo "Instala Docker Compose V2 o actualiza Docker Desktop"
        exit 1
    fi
    
    log_info "✓ Dependencias verificadas - usando Docker Compose V2"
}

# Limpiar recursos previos
cleanup() {
    log_info "Limpiando despliegue anterior..."
    docker compose down --remove-orphans
}

# Construir y desplegar
deploy() {
    log_info "Construyendo y desplegando servicios..."
    
    # Construir imágenes
    if docker compose build --no-cache; then
        log_info "✓ Imágenes construidas correctamente"
    else
        log_error "Fallo en la construcción de imágenes"
        exit 1
    fi
    
    # Levantar servicios
    if docker compose up -d; then
        log_info "✓ Servicios desplegados correctamente"
    else
        log_error "Fallo al desplegar servicios"
        exit 1
    fi
}

# Verificar salud de los servicios
health_check() {
    log_info "Verificando salud de los servicios..."
    
    # Wait for database health status
    log_info "Esperando a que la base de datos esté lista..."
    local db_attempt=1
    local max_db_attempts=30
    
    while [ $db_attempt -le $max_db_attempts ]; do
        if docker compose exec -T database pg_isready -U borme_user -d borme > /dev/null; then
            log_info "✓ Base de datos está lista después de $((db_attempt * 2)) segundos"
            break
        else
            sleep 2
            ((db_attempt++))
        fi
    done
    
    # Quick check if backend is responsive
    if curl -s -f http://localhost:8080/actuator/health > /dev/null; then
        log_info "✓ Backend está respondiendo"
        return 0
    else
        log_warn "Backend no responde inmediatamente, pero los healthchecks de Docker lo manejarán"
        return 0
    fi
}

# Probar endpoints básicos
test_endpoints() {
    log_info "Probando endpoints de la API..."
    
    if curl -s http://localhost:8080/api/empresas/estadisticas | grep -q "totalEmpresas"; then
        log_info "✓ Endpoint de estadísticas funcionando"
    else
        log_warn "⚠ Endpoint de estadísticas no responde como esperado"
    fi
    
    if curl -s http://localhost:8080/api/pdfs/health | grep -q "status"; then
        log_info "✓ Endpoint de PDFs funcionando"
    else
        log_warn "⚠ Endpoint de PDFs no responde como esperado"
    fi
}

# Mostrar información del despliegue
show_info() {
    log_info "=== DESPLIEGUE COMPLETADO ==="
    echo ""
    echo " SERVICIOS DESPLEGADOS:"
    echo "    Backend API:    http://localhost:8080"
    echo "    Health Check:   http://localhost:8080/actuator/health"
    echo "    API Empresas:   http://localhost:8080/api/empresas/estadisticas"
    echo ""
    echo "  COMANDOS DE GESTIÓN:"
    echo "   Ver todos los logs:    docker compose logs -f"
    echo "   Ver logs backend:      docker compose logs -f backend"
    echo "   Ver logs database:     docker compose logs -f database"
    echo "   Detener servicios:     docker compose down"
    echo "   Estado servicios:      docker compose ps"
    echo "   Reiniciar backend:     docker compose restart backend"
    echo ""
    echo " SEGURIDAD:"
    echo "    Base de datos aislada - solo accesible desde la red interna Docker"
    echo "    Backend construido dentro de contenedor - no depende del host"
    echo ""
    echo " DATOS PERSISTENTES:"
    echo "   Volumen BD:          borme_db_data"
    echo "   Volumen PDFs:        borme_pdf_storage"
    echo ""
    log_info "Listo para usar! "
}

# Función principal
main() {
    log_info "Iniciando despliegue de BORME API con Docker Compose V2..."
    
    check_dependencies
    cleanup
    deploy
    health_check
    test_endpoints
    show_info
}

# Ejecutar función principal
main "$@"
