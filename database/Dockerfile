FROM postgres:13

# Copiar script de inicialización
COPY init.sql /docker-entrypoint-initdb.d/

# Dar permisos
RUN chmod a+r /docker-entrypoint-initdb.d/init.sql


