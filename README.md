# Consulta empresas del BORME

Este repositorio contiene los programas desarrollados para, dado un día concreto en formato "YYYY-MM-DD", localizar las constituciones de sociedades publicadas en los ficheros PDF del BORME, descargarlas, extraer su información de forma estructurada y exponerlas a través de una API para su posterior consulta desde una interfaz web en Svelte, que muestra los resultados en una tabla con distintos filtros, y permite acceder al detalle de cada constitución, incluyendo la visualización del PDF original.

## Tabla de contenidos

- [Requisitos](#requisitos)
- [Preparación del entorno](#preparación-del-entorno)
  - [Requisitos Previos](#requisitos-previos)
- [Uso](#uso)
- [API REFERENCE](#api-reference)



## Preparación del entorno

### Requisitos Previos

- **Java 11 o superior**  
  (probado con OpenJDK 17.0.16)
- **Spring Boot 2.7.18**
- **Gradle 7.x o superior**  
  (el proyecto incluye `gradlew`, por lo que no es necesario tener Gradle instalado globalmente)

- **Node.js 22.20 o superior**  
  (requerido por `create-vite@8.0.2`)
- **pnpm** (gestor de paquetes para el frontend)
  
  
- **Docker 20.10**  
  (probado con Docker 27.5.1)
- **Docker Compose 2.40**  
  (plugin de Docker, utilizado mediante `docker compose`)

### Entorno de pruebas

Esta aplicación ha sido probada en Ubuntu 24.04 pero debería ser capaz de ejecutarse en cualquier distribución de Linux si se hace mediante Docker.


## Uso

### Dar permisos a los scripts

```bash
chmod +x deploy.sh manage.sh
```

### Desplegar completo
```bash
./deploy.sh
```

### O usar el gestor para operaciones específicas
```bash
./manage.sh start     # Iniciar servicios
```
```bash
./manage.sh status    # Ver estado
```
```bash
./manage.sh logs      # Ver logs
```
```bash
./manage.sh stop      # Detener servicios
```
```bash
./manage.sh stopAll    # Detener servicios y borrar volúmenes
```

### Comandos Docker Compose V2 directos
```bash
docker compose ps               # Estado de servicios
```
```bash
docker compose logs -f <service>  # Logs del servicio (backend, frontend, database)
```
```bash
docker compose restart <service>  # Reiniciar solo el servicio indicado (backend, frontend, database)
```

### Acceso a la aplicación
Una vez desplegado, se puede acceder a la aplicación en [http://localhost](http://localhost)



## API Reference

### Endpoints Disponibles

#### **Empresas/Constituciones**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/empresas/fecha/{fecha}` | Obtiene constituciones para una fecha específica (formato: YYYY-MM-DD). Si no existen datos, los descarga y procesa automáticamente |
| `GET` | `/api/empresas/buscar?nombre={texto}` | Busca constituciones por nombre de empresa (búsqueda parcial) |
| `GET` | `/api/empresas` | Obtiene todas las constituciones almacenadas en la base de datos |
| `GET` | `/api/empresas/{numeroAsiento}/{fechaConstitucion}` | Obtiene los detalles de una constitución específica |
| `GET` | `/api/empresas/estadisticas` | Obtiene estadísticas generales (total empresas, fechas, etc.) |

#### **PDFs**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/pdfs/{fecha}/{nombreArchivo}` | Sirve el archivo PDF original para visualización |
| `GET` | `/api/pdfs/{fecha}/{nombreArchivo}/existe` | Verifica si un PDF existe en el sistema |

#### **BORME (Consultas directas)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/borme/{fecha}` | Consulta el BORME para una fecha específica (HTML crudo) |
| `GET` | `/api/borme/hoy` | Consulta el BORME para la fecha actual |
| `GET` | `/api/borme/validar/{fecha}` | Valida si una fecha es válida para consultar el BORME |



