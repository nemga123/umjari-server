# Umjari

1. start server
   1. in local
    ```shell
    $ ./gradlew bootRun --args='--spring.profiles.active=local'
    ```
   2. by docker
    ```shell
    $ ./gradlew bootJar
    $ docker-compose up --build -d
    ```