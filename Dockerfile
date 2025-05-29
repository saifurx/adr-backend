FROM maven:3.9.9-amazoncorretto-21-debian AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install package

FROM openjdk:21
COPY --from=build /home/app/target/adr-0.0.1-SNAPSHOT.jar /usr/local/lib/adr-backend.jar
EXPOSE 7000
ENTRYPOINT ["java","-jar","/usr/local/lib/adr-backend.jar"]
