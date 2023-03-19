# Umjari

## 1. start server
   1. in local
    ```shell
    $ ./gradlew bootRun --args='--spring.profiles.active=local'
    ```
   2. by docker
    ```shell
    $ ./gradlew bootJar
    $ docker-compose up --build -d
    ```

## 2. Swagger
   http://localhost:8080/swagger-ui/index.html
   <br/>
   swagger api docs