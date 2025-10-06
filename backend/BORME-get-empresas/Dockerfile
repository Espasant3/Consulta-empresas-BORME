# Fase de construcción
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Copiar solo los archivos necesarios para la construcción
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

# Dar permisos y construir
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

# Fase de ejecución
FROM openjdk:17-jdk-slim

WORKDIR /app

# Instalar curl solo para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear directorio para PDFs
RUN mkdir -p /app/pdfs_descargados && chmod 755 /app/pdfs_descargados

# Copiar el JAR construido
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Usar un usuario no-root para mayor seguridad
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]