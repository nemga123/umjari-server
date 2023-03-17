FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

EXPOSE 8080

CMD java -jar -Dspring.profiles.active=docker build/libs/server-0.0.1-SNAPSHOT.jar