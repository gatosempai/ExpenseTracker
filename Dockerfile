FROM gradle:8.12.0-jdk17 AS build
WORKDIR /app
COPY gradlew /app/gradlew
COPY gradle/wrapper/gradle-wrapper.jar /app/gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.properties /app/gradle/wrapper/
COPY gradle/libs.versions.toml /app/gradle/libs.versions.toml
COPY settings.gradle.kts /app/
COPY gradle.properties /app/
COPY build.gradle.kts /app/
COPY backend /app/backend
RUN gradle :backend:installDist --no-daemon --build-cache -x :backend:test

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/backend/build/install/backend /app
USER app
EXPOSE 8080
ENV SERVER_PORT=8080
CMD ["/app/bin/backend"]
