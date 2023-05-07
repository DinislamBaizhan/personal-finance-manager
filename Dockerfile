FROM maven:4.0.0-openjdk-17 AS build
COPY target/*.jar Auth-server.jar
RUN mvn clean package -Pprod -DskipTests

#
# Package stage
#
FROM openjdk:11-jdk-slim
COPY --from=build /target/Auth-server-0.0.1-SNAPSHOT.jar Auth-server.jar

ENTRYPOINT ["java","-jar","Auth-server.jar"]