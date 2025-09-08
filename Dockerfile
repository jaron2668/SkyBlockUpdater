# Stage 1: Build with Maven + JDK 21
FROM maven:3.9.11-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy shared models first and install it into local repo
COPY ../SkyblockSharedModels /app/SkyblockSharedModels
RUN mvn -f /app/SkyblockSharedModels/pom.xml clean install -DskipTests

# Copy and build the microservice
COPY pom.xml .
COPY src ./src
COPY THIRD-PARTY-LICENSES.txt .
COPY LICENSE.txt .
RUN mvn clean package -DskipTests

# Stage 2: Runtime with JDK 21
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/SkyblockUpdater-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]