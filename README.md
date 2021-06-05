
# eVehicle advertising Rest Api
- Built with Spring Boot
## Commands:
- mvn package
### Start with in-memory db :
 (initial user credentials: username: admin, password: admin)
- java -jar ./eVehicle-advertising-service/target/eVehicle-advertising-service-0.0.1-SNAPSHOT.jar
### Start in production mode, MySQL server must be running  :
- java -jar -Dspring.profiles.active=prod ./eVehicle-advertising-service/target/eVehicle-advertising-service-0.0.1-SNAPSHOT.jar 

- http://localhost:8080/swagger-ui.html 

<img src="documentation.png" width="900px">