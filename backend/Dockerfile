# Usa una imagen oficial de Java
FROM openjdk:17-jdk-slim

# Crea el directorio de la app dentro del contenedor
WORKDIR /app

# Copia el archivo jar ya compilado (debes haber hecho `mvn package`)
COPY target/*.jar app.jar

# Expone el puerto 8081 (ajústalo si usas otro)
EXPOSE 8081

# Comando para correr el .jar
ENTRYPOINT ["java", "-jar", "app.jar"]
