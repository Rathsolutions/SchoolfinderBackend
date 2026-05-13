FROM eclipse-temurin:21
WORKDIR /usr/schoolfinderbackendnew/
COPY target/SchoolfinderBackend-0.0.1-SNAPSHOT.jar  Backend.jar
ENTRYPOINT ["java", "-jar","-Xmx4096M","-Xms512M","-server", "Backend.jar"]
