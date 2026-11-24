# Imagen para desplegar el backend en Render
# Etapa 1: compila el proyecto con Maven (sin correr pruebas)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

# Etapa 2: solo el jar con Java 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Render gratis tiene 512 MB de memoria
ENV JAVA_OPTS="-Xmx350m -XX:+UseSerialGC"
EXPOSE 8090
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
