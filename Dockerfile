FROM adoptopenjdk
WORKDIR /usr/schoolfinderbackend/
COPY target/SchoolfinderBackend-0.0.1-SNAPSHOT.jar  Backend.jar
ENTRYPOINT ["java", "-jar", "Backend.jar"]
