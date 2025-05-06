# Usa una imagen oficial de Java y Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Copia el proyecto y compílalo
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Usa una imagen base más liviana solo con Java
FROM eclipse-temurin:17
WORKDIR /app

# Copia el JAR compilado
COPY --from=build /app/target/DiscordBot-1.0-SNAPSHOT.jar app.jar

# Ejecuta el bot
CMD ["java", "-jar", "app.jar"]
