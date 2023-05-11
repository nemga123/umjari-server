# Umjari
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)



## 1. start server
   1. in local
   
    $ ./gradlew bootRun --args='--spring.profiles.active=local'
   2. by docker
   
    $ ./gradlew bootJar
    $ docker-compose up --build -d

## 2. Swagger
   http://localhost:8080/swagger-ui/index.html
   <br/>
   swagger api docs
