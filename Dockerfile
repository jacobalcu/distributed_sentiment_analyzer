# Use base image w Java 25 (or 17/21 if 25 not avail)
#FROM eclipse-temurin:21-jdk-alpine
FROM eclipse-temurin:25-jdk

# Set working directory inside container
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching dependencies)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (step is cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Run the jar file
CMD ["java", "-jar", "target/sentiment-crawler-0.0.1-SNAPSHOT.jar"]