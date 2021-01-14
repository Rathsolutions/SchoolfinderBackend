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

## OSM File Generation
To generate some new osm files (regardless if you are generating a school file, a city file or a street file) you will need the osmosis tool. The following steps should help you to generate the correct file for your purpose

### Generic Generation
1. Download the baden_wuerttemberg.osm.pbf file & download osmosis
2. See the specific generation instructions below for school specific and city specific
3. Edit the xml file; Add the correct position to all of the ways (you can generate them based on the included nodes)
4. Run the test 'testReducing()' from the testclass 'de.rathsolutions.OsmPOIReducerTest' to reduce the complexity of the freshly generated file (outcomment the @Disabled annotation, but dont forget to reenable it)
   4.1 You have to change the used file in the OsmPOIReducer class to your file
5. Put your newly generated file into src/test/resources and src/main/resources and rename it to the corresponding original filename

### School Specific
2. Extract all of amenity school, college, university (only nodes and ways, ignore relations)

### City Specific
2. Extract all of place city, village, suburb, town (only nodes and ways, ignore relations)

### Street-City Specific
To build the street city specific file, you have to follow different instructions:

2. Extract all of nodes and ways containing tag attributes addr:city or addr:suburb or both
4. Reduce its complexity; break the freshly generated file down by removing every entry which is not equal to addr:city, addr:street, addr:housenumber, addr:suburb 
6. Run the test 'writeHeapFile' from the testclass OsmStreetParserTest (outcomment the @Disabled annotation, but dont forget to reenable it)
7. All done! The heapfile under src/main/resources contains all of the street and city entries

## Build
If you want to build the project, you need the following tools:

- Java 14
- Maven

After satisfying those requirements, just run mvn clean package spring-boot:run to immediatly start an embedded tomcat server. For building a jar, all you have to do is running a mvn clean package spring-boot:repackage
