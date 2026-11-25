# Render no tiene un entorno de Java propio: el backend se publica como imagen Docker.

# 1) Compilar el .jar con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# 2) Imagen final solo con Java para ejecutar el .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# MaxRAMPercentage: que Java no use mas memoria de la que da el plan gratis de Render
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
