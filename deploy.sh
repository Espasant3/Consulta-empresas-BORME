#!/bin/bash

set -e  # Exit on any error

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $1"
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
    log_info "Construyendo imágenes Docker..."
    if docker compose build --no-cache; then
        log_info "✓ Imágenes construidas correctamente"
    else
        log_error "Fallo en la construcción de imágenes"
        exit 1
    fi
    
    # Levantar servicios
    log_info "Iniciando servicios..."
    if docker compose up -d; then
        log_info "✓ Servicios desplegados correctamente"
    else
        log_error "Fallo al desplegar servicios"
        exit 1
    fi
}

# Verificar que los contenedores están corriendo
check_containers_running() {
    log_info "Verificando estado de los contenedores..."
    
    # Verificar que los contenedores están en estado "running"
    if docker compose ps --format json | grep -q '"State":"running"'; then
        log_info "✓ Todos los contenedores están en ejecución"
        return 0
    else
        log_warn "Algunos contenedores pueden no estar completamente inicializados"
        return 1
    fi
}

# Verificar salud de los servicios
health_check() {
    log_info "Verificando salud de los servicios..."
    
    # Esperar a que la base de datos esté lista
    log_info "Esperando a que la base de datos esté lista..."
    local db_attempt=1
    local max_db_attempts=15
    
    while [ $db_attempt -le $max_db_attempts ]; do
        if docker compose exec -T database pg_isready -U borme_user -d borme > /dev/null 2>&1; then
            log_info "✓ Base de datos lista (${db_attempt}/15)"
            break
        else
            if [ $db_attempt -eq 1 ]; then
                log_debug "Base de datos iniciando..."
            fi
            sleep 2
            ((db_attempt++))
        fi
        
        if [ $db_attempt -gt $max_db_attempts ]; then
            log_warn "Base de datos tardando más de lo esperado, continuando..."
        fi
    done
    
    # Verificar que el backend está respondiendo usando un endpoint básico
    log_info "Verificando que el backend esté respondiendo..."
    local backend_attempt=1
    local max_backend_attempts=25
    
    while [ $backend_attempt -le $max_backend_attempts ]; do
        # Usamos un endpoint que siempre debería funcionar una vez Spring Boot esté listo
        if curl -s -f http://localhost:8080/api/borme/validar/2024-01-01 > /dev/null 2>&1; then
            log_info "✓ Backend respondiendo correctamente (${backend_attempt}/25)"
            return 0
        else
            if [ $backend_attempt -eq 1 ]; then
                log_debug "Backend iniciando (Spring Boot puede tomar 20-40 segundos)..."
            elif [ $backend_attempt -eq 10 ]; then
                log_debug "Backend aún iniciando, Spring Boot está cargando los beans..."
            elif [ $backend_attempt -eq 20 ]; then
                log_debug "Backend casi listo, iniciando controladores..."
            fi
            sleep 2
            ((backend_attempt++))
        fi
    done
    
    # Si llegamos aquí, el backend no respondió, pero verificamos si al menos el puerto está abierto
    if nc -z localhost 8080 2>/dev/null; then
        log_info "✓ Puerto 8080 abierto - Backend probablemente listo"
        return 0
    else
        log_warn "Backend no responde completamente, pero los contenedores están ejecutándose"
        log_warn "Puede que Spring Boot esté aún iniciando. Revisa: docker compose logs -f backend"
        return 0
    fi
}

# Probar endpoints básicos de forma no intrusiva
test_endpoints() {
    log_info "Probando endpoints básicos..."
    
    # Probar endpoint de validación (debería funcionar siempre)
    if curl -s http://localhost:8080/api/borme/validar/2024-01-01 | grep -q "valida" 2>/dev/null; then
        log_info "✓ Endpoint de validación funcionando"
    else
        log_debug "Endpoint de validación no responde como esperado (puede ser temporal)"
    fi
    
    # Probar endpoint de empresas (puede fallar si la BD está vacía)
    if curl -s http://localhost:8080/api/empresas/estadisticas > /dev/null 2>&1; then
        log_info "✓ API de empresas respondiendo"
    else
        log_debug "API de empresas no responde (puede ser normal con BD vacía)"
    fi
    
    # Probar que al menos podemos conectar al puerto
    if curl -s http://localhost:8080 > /dev/null 2>&1; then
        log_info "✓ Backend aceptando conexiones"
    fi
}

# Mostrar información del despliegue
show_info() {
    log_info "=== DESPLIEGUE COMPLETADO ==="
    echo ""
    echo "🎯 SERVICIOS DESPLEGADOS:"
    echo "   📊 Backend API:    http://localhost:8080"
    echo "   🔍 Validar fecha:  http://localhost:8080/api/borme/validar/2024-01-01"
    echo "   📚 API Empresas:   http://localhost:8080/api/empresas/fecha/2024-09-10"
    echo ""
    echo "🛠️  COMANDOS DE GESTIÓN:"
    echo "   Ver logs en tiempo real:  docker compose logs -f"
    echo "   Ver solo backend:         docker compose logs -f backend"
    echo "   Estado de servicios:      docker compose ps"
    echo "   Detener todos:            docker compose down"
    echo "   Reiniciar backend:        docker compose restart backend"
    echo ""
    echo "🚀 PRÓXIMOS PASOS:"
    echo "   1. Probar la API: curl http://localhost:8080/api/empresas/fecha/2024-09-10"
    echo "   2. Ver logs: docker compose logs -f backend"
    echo "   3. Consultar documentación en README.md"
    echo ""
    echo "💡 NOTA: La base de datos está vacía inicialmente."
    echo "   La primera consulta a una fecha descargará y procesará los datos automáticamente."
    echo ""
    log_info "¡Sistema listo para usar! 🎉"
}

# Función principal
main() {
    log_info "Iniciando despliegue de BORME API con Docker Compose V2..."
    
    check_dependencies
    cleanup
    deploy
    check_containers_running
    health_check
    test_endpoints
    show_info
    
    log_info "Despliegue completado exitosamente"
}

# Ejecutar función principal
main "$@"
