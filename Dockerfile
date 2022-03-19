FROM openjdk:8-jre-alpine

RUN apk add ttf-dejavu

COPY target/reports-0.0.1-SNAPSHOT.jar /app/reports.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT java -jar reports.jar