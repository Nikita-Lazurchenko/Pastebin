FROM gradle:8.12-jdk23 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle bootWar -x test --no-daemon


FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.war app.war

ENTRYPOINT ["java", "-jar", "app.war"]