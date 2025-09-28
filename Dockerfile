FROM docker.io/maven:3-eclipse-temurin-25-alpine AS maven
WORKDIR opt
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn package

FROM docker.io/eclipse-temurin:25-alpine
WORKDIR opt/tcx-to-json/target
COPY --from=maven opt/target/tcx-to-json.jar .
CMD java -jar tcx-to-json.jar
