# Etapa 1: build con Maven (imagen mantenida; openjdk oficial ya no existe en Docker Hub)
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: solo JRE para ejecutar el jar (imagen más liviana)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/ovycar-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
