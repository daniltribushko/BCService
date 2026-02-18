FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN gradle build -x text || return 0

COPY . .

RUN gradle clean build -x test

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar geo_app.jar

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "geo_app.jar"]