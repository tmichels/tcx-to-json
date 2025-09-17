FROM docker.io/maven:3-eclipse-temurin-21-alpine AS maven
WORKDIR opt
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn package

FROM docker.io/openjdk:25
WORKDIR opt/tcx-to-json/target
COPY --from=maven opt/target/tcx-to-json.jar .
CMD java -jar tcx-to-json.jar
