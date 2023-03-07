FROM eclipse-temurin
WORKDIR /usr/schoolfinderbackendnew/
COPY target/SchoolfinderBackend-0.0.1-SNAPSHOT.jar  Backend.jar
ENTRYPOINT ["java", "-jar","-Xmx1400M","-Xms512M","-server", "Backend.jar"]
