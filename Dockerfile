FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Gradle wrapper and build files first (better layer caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Download dependencies separately so they're cached unless build files change
RUN ./gradlew dependencies --no-daemon || true

# Now copy source and build
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as non-root user (security best practice)
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]