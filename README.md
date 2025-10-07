# Consulta empresas del BORME

Este repositorio contiene los programas desarrollados para, dado un día concreto en formato "YYYY-MM-DD", localizar las constituciones de sociedades publicadas en los ficheros PDF del BORME, descargarlas, extraer su información de forma estructurada y exponerlas a través de una API para su posterior consulta desde un frontend por determinar.

## Tabla de contenidos

- [Requisitos](#requisitos)
- [Preparación del entorno](#preparación-del-entorno)
  - [Requisitos Previos](#requisitos-previos)
  - [Docker](#docker)
- [Uso](#uso)



## Preparación del entorno

### Requisitos Previos

- **Java 11 o superior**  
  (probado con OpenJDK 17.0.16)
- **Spring Boot 2.7.18**
- **Gradle 7.x o superior**  
  (el proyecto incluye `gradlew`, por lo que no es necesario tener Gradle instalado globalmente)
- **Docker 20.10**  
  (probado con Docker 27.5.1)
- **Docker Compose v2**  
  (incluido como plugin en Docker 27.5.1, utilizado mediante `docker compose`)





### Docker


## Uso

### Dar permisos a los scripts
chmod +x deploy.sh manage.sh

### Desplegar completo (recomendado)
./deploy.sh

### O usar el gestor para operaciones específicas
./manage.sh start     # Iniciar servicios
./manage.sh status    # Ver estado
./manage.sh logs      # Ver logs
./manage.sh stop      # Detener servicios

### Comandos Docker Compose V2 directos
docker compose ps              # Estado de servicios
docker compose logs -f backend # Logs del backend
docker compose restart backend # Reiniciar solo backend


### Comandos útiles


# Construir y ejecutar localmente
./gradlew bootRun

# Construir JAR
./gradlew bootJar

# Ejecutar tests
./gradlew test

# Construir con Docker
docker build -t borme-scraper .

# Ejecutar con Docker
docker run -p 8080:8080 -v $(pwd)/pdfs_descargados:/app/pdfs_descargados borme-scraper

# Ejecutar todo con Docker Compose
docker-compose up -d

# Ver logs
docker-compose logs -f borme-scraper

# Parar servicios
docker-compose down

# Limpiar completamente
docker-compose down -v


