# Dockerfile para el frontend
FROM node:18-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos del proyecto
COPY . .

# Instalar dependencias
RUN npm install

# Construir la aplicación
RUN npm run build

# Exponer el puerto del frontend
EXPOSE 3000

# Comando para ejecutar la aplicación
CMD ["npm", "start"]
