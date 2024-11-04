# tiny-bank-management-service

Test project for tiny bank management service

## Requirements

For building and running the application you need:

- [JDK 17](https://www.azul.com/downloads/?version=java-17-lts&os=linux&package=jdk#zulu)
- [Maven 3](https://maven.apache.org)
- [Spring Boot 3.3.5](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.3-Release-Notes)

## Building a fat jar

```shell
cd ./tiny-bank-management-service
mvn clean package
```

## Testing the application

```shell
cd ./tiny-bank-management-service
mvn clean test
```

## How to Test case coverage

```shell
cd ./tiny-bank-management-service
mvn clean install
awk -F, '{ instructions += $4 + $5; covered += $5 } END { print covered, "/", instructions, " instructions covered"; print 100*covered/instructions, "% covered" }' ./coverage-mo
dule/target/site/jacoco-aggregate/jacoco.csv
```
Please find the detailed test case report: [/tiny-bank-management-service/coverage-module/target/site/jacoco-aggregate/index.html]()

## How to Run
```shell
java -jar ./tiny-bank-management-service/tiny-bank-service.jar
```

## Running the application locally

```shell
mvn spring-boot:run
```

## Swagger Url

http://localhost:8000/swagger-ui/index.html

## Implementation details

#### Initializer used:

[Spring Initializer](https://start.spring.io/)

#### Application Properties:

This section describes the configurable properties for the application. These properties can be set in
the `application.yml` file or as environment variables.

##### General Configuration

| Property Name                           | Default Value | Description             |
|-----------------------------------------|---------------|-------------------------|
| `logging.level.com.tinybank.management` | `INFO`        | The root logging level. |

