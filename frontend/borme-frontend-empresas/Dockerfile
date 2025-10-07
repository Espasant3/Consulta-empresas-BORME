# Etapa 1: build
FROM node:22-alpine AS build

WORKDIR /app

# Copiar dependencias primero
COPY package.json pnpm-lock.yaml* ./

# Instalar pnpm
RUN npm install -g pnpm

# Instalar dependencias
RUN pnpm install --frozen-lockfile

# Copiar el resto del código
COPY . .

# Construir la aplicación
RUN pnpm run build

# Etapa 2: producción
FROM nginx:stable-alpine AS production

# Copiar el build generado a la carpeta de Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Opcional: Copiar configuración personalizada de nginx si la tienes
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]