# Use Maven to build the app
FROM maven:3.9.5-eclipse-temurin-17 as build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Use lightweight JDK image to run the app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/secret-santa-0.0.1-SNAPSHOT.jar app.jar

# Set config location to external directory
ENV SPRING_CONFIG_LOCATION=file:/config/

ENTRYPOINT ["java", "-jar", "app.jar"]
