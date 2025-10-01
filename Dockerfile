FROM maven:3.9.3-eclipse-temurin-17 AS dev
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.fork=false"]
