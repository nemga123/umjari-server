FROM gradle:7.6.1-jdk17

WORKDIR /app

COPY . .

EXPOSE 8080

RUN gradle clean build --no-daemon

CMD java -jar build/libs/server-0.0.1-SNAPSHOT.jar