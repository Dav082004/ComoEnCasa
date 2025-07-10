# Usa una imagen oficial con Java 21
FROM eclipse-temurin:21-jdk

# Establece el directorio de trabajo
WORKDIR /app

# Copia el contenido del backend
COPY . .

# Da permisos de ejecución al wrapper de Maven y construye sin tests
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# Expone el puerto por el que se ejecuta Spring Boot
EXPOSE 8081

# Ejecuta el .jar generado
CMD ["java", "-jar", "target/comoencasa-backend-0.0.1-SNAPSHOT.jar"]
