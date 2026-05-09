# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy all module poms to satisfy parent module list
COPY inventory-service/pom.xml inventory-service/
COPY order-service/pom.xml order-service/

# Copy inventory-service source
COPY inventory-service/src inventory-service/src

# Build the specific module
RUN mvn -pl inventory-service -am clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/inventory-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
