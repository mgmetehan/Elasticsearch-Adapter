# Elasticsearch-Spring-Data-Demo

### Tech Stack
- Java 17
- Spring Boot 3.0
- Spring Elasticsearch Data
- Docker
- Lombok

### Requirements

For building and running the application you need:
- JDK 17 or newer
- [Maven](https://maven.apache.org)
- [Lombok](https://projectlombok.org/)


### Build & Run
```
  docker-compose -f docker-compose.yml up -d
```
```
  mvn clean install && mvn --projects backend spring-boot:run
```