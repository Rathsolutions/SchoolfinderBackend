# SchoolfinderBackend
The backend module of the project schoolfinder

## Configuration

If you are familiar with the spring-framework, you will notice that the application.yml file under src/main/resources is not existing. You have to add this file, containing 
the configuration of a working datasource. This datasource can either be a standalone mariadb instance or an embedded datasource. The application.yml file
should look like this:

### Standalone Mariadb
``` yaml
spring:
  datasource:
     url: jdbc:mysql://HOST:PORT/DATABASE
     username: USERNAME
     password: PASSWORD
     driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
```
### Embedded H2
``` yaml
spring:
  datasource:
     url: jdbc:h2:file:./db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
     username: sa
     password: sa
     driver-class-name: org.h2.Driver
  jpa:
     hibernate:
     ddl-auto: update
```
If you want to use an embedded h2 database, you have to change the database driver in the pom.xml to the h2 driver

## Build
If you want to build the project, you need the following tools:

- Java 14
- Maven

After satisfying these requirements, just run mvn clean package spring-boot:run to immediatly start an embedded tomcat server. For building a jar, all you have to do is running a mvn clean package spring-boot:repackage
