# Etapa 1: Compilar el código usando Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
# Copiamos el pom.xml y descargamos dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copiamos el código fuente y compilamos el .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final y ligera para correr la aplicación
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Extraemos el .jar compilado desde la etapa 1
COPY --from=builder /app/target/microservicio-2-catalogo-restaurantes-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
