## Instructions
#### Project Details :
1. This project is built with Spring boot and [Influx DB](https://www.influxdata.com/) (Time series Database)
2. Mockito framework is used to write test cases for the apis.

#### Execution Steps
1. This [project] can be executed locally by using docker compose tool.A file **docker-compose.yml** has been provided as a part of the project.
   Steps: 
   1. Clone the project from [repository]().
   2. run `docker-compose up -d` .**Prerequisite [Docker Desktop](https://www.docker.com/products/docker-desktop)**
2. The same project can be executed locally using any IDE (Spring boot supported)
   1. If running locally :
      1. Either Download docker image for influxdb or the below command might be used (not tested)
         `docker run --rm --name influx-temp -p 8086:8086 \
         -e INFLUXDB_DB=temperature-db \
         -e DOCKER_INFLUXDB_INIT_MODE=setup \
         -e DOCKER_INFLUXDB_INIT_USERNAME=user \
         -e DOCKER_INFLUXDB_INIT_PASSWORD=userpass \
         -e INFLUXDB_ADMIN_USER=admin \
         -e INFLUXDB_ADMIN_PASSWORD=passw0rd \
         -e INFLUXDB_USER=user \
         -e INFLUXDB_USER_PASSWORD=userpass \
         -e DOCKER_INFLUXDB_INIT_ORG=qardio \
         -e DOCKER_INFLUXDB_INIT_BUCKET=temperature-bucket \
         -e DOCKER_INFLUXDB_INIT_RETENTION=2d \
         -v influxdb:/var/lib/influxdb \
         influxdb`
      2. Or Please follow this [link] to download and install influxdb on host machine.(https://docs.influxdata.com/influxdb/v2.0/install/)
      3. If docker desktop is not installed , then the application can also be run locally:
         * Open terminal/cmd and navigate to the project folder stored locally.
         * Once done please execute : ./mvnw spring-boot:run
3. Once above steps are done , then please open [influxdb console] (http://localhost:8086/)
   1. If prompted for user creds then type admin/passw0rd
   2. If prompted with user creation form then please fill below details
            _username_ : admin 
            _password_: passw0rd
            _organizationname_ : qardio
4. If all above steps are done then please follow below section to check the application 
   * api to insert data : This api is storing data to influxdb on a batch mode.For testing the batch size is set to 10.
     API : `http://localhost:8091/temperature-data/save`
     Request Method : POST
     Request Body : JSON 
     Sample Request : 
     ```json
     {
     "clientName": "test",
     "temperatures": ["88", "104", "34.5", "12"]
     }
     ```
   * api aggregate : This api fetches the aggregated data from influxDB using Flux query languages.For sampling , 
    the range is set for 7 days from the time of testing.
     API : `http://localhost:8091/temperature-data/aggregator/byhour`
     Request Method : GET
     Query Parameter : byhour -> retrieves hourly data
                       byday -> retrieves daily data
     Sample response :
    ```json
      [
      {
        "temperature": "2.0",
        "resultTime": "2021-10-06T21:00:00Z"
      },
      {
        "temperature": "50.49961538461537",
        "resultTime": "2021-10-06T22:17:51.933533Z"
      }
      ] 
    ```
    

#### For any issues please send an email to [me](mailto:cjrocks911@gmail.com) 


    