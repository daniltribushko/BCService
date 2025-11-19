FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1 AS build
WORKDIR /app

COPY project/ project/
COPY build.sbt ./

RUN sbt update

COPY src/ src/

RUN sbt assembly

FROM openjdk:21-rc-jdk-slim

WORKDIR /app

COPY --from=build /app/target/scala-*/*.jar /app/user_app.jar

ENTRYPOINT ["java", "-jar", "/app/user_app.jar"]