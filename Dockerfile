############### BUILD STAGE ###############
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY backend/pom.xml .
COPY backend/src ./src

RUN mvn clean package -DskipTests

############### RUN STAGE ###############
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy jar (auto-detect the correct name)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
