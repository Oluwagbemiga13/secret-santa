FROM maven:3.9.5-eclipse-temurin-17 as build
WORKDIR /app

# Add volume mount point
VOLUME /root/.m2

COPY pom.xml .
COPY src ./src

ARG MAVEN_OPTS
RUN mvn clean package -DskipTests

# Use lightweight JDK image to run the app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/secret-santa-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
