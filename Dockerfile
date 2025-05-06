# Etapa de construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa final
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/DiscordBot-1.0-SNAPSHOT.jar app.jar

# Ejecutar el bot
CMD ["java", "-jar", "app.jar"]
