FROM maven:3-amazoncorretto-21-al2023 AS maven
WORKDIR opt
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn package

FROM amazoncorretto:21
WORKDIR opt/tcx-to-json/target
COPY --from=maven opt/target/tcx-to-json-1.0.jar .
CMD java -jar tcx-to-json-1.0.jar
