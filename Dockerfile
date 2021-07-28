#
# Build stage
#
FROM maven:3.8.1-jdk-11-openj9 AS build
WORKDIR /home/webserver
COPY ./ ./
RUN mvn clean package

#
# Package stage
#
FROM openjdk:11.0.12-jdk-oracle
WORKDIR /home/app
COPY --from=build /home/webserver/eVehicle-advertising-service/target/eVehicle-advertising-service-0.0.1-SNAPSHOT.jar ./
COPY --from=build /home/webserver/wait.sh ./
COPY --from=build /home/webserver/images ./images
EXPOSE 8080
ENTRYPOINT ["./wait.sh", "mysql_db:3306" , "--" ,"java","-jar","-Dspring.profiles.active=prod","eVehicle-advertising-service-0.0.1-SNAPSHOT.jar"]