FROM maven:3-amazoncorretto-21-al2023 AS maven
WORKDIR opt
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn package

FROM amazoncorretto:21
WORKDIR opt/backend-xsd-reader/target
COPY --from=maven opt/target/backend-xsd-reader-1.0.jar .
CMD java -jar backend-xsd-reader-1.0.jar
