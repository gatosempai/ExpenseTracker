FROM gradle:8.12.0-jdk17 AS build
WORKDIR /app
COPY --chown=gradle:gradle ../gradle /app/gradle
COPY --chown=gradle:gradle ../gradlew /app/gradlew
COPY --chown=gradle:gradle ../settings.gradle.kts /app/
COPY --chown=gradle:gradle ../gradle.properties /app/
COPY --chown=gradle:gradle ../build.gradle.kts /app/
COPY --chown=gradle:gradle ../backend /app/backend
RUN gradle :backend:installDist --no-daemon --build-cache -x :backend:test

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/backend/build/install/backend /app
USER app
EXPOSE 8080
ENV SERVER_PORT=8080
CMD ["/app/bin/backend"]
