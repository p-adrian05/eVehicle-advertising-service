#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /home/webserver
COPY ./ ./
RUN mvn clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
WORKDIR /home/app
COPY --from=build /home/webserver/eVehicle-advertising-service/target/eVehicle-advertising-service-0.0.1-SNAPSHOT.jar ./
COPY --from=build /home/webserver/wait-for-it.sh ./
COPY --from=build /home/webserver/images ./images
EXPOSE 8080
ENTRYPOINT ["./wait-for-it.sh", "mysql_db:3306" , "--" ,"java","-jar","-Dspring.profiles.active=prod","eVehicle-advertising-service-0.0.1-SNAPSHOT.jar"]