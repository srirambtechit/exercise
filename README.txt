Notes:

## TOOLS AND CONFIGS ##
- Java 8
- Maven 3.5
- Spring Boot 2.4.5
- Docker compose 1.29.0
- Docker Desktop 3.3.1
- Postman 8.2.3
- IntelliJ IDEA 2021.1
  - Make sure Annotation-based processing enabled in settings (generally not required, if in case any error)


## STEPS TO RUN THE PROJECT ##
# Please make sure above mentioned tools and its compbatilible version is in place before running
# extract archive and navigate
$ tar -xvf jobmgmt.tar.gz -C /tmp
$ cd /tmp/jobmgmt/image-processor

# build project and docker image
$ mvn clean package
$ docker build --rm --tag=image-processor:latest .

# start the docker cluster using docker-compose command
# one directory up where docker-compose.yml file present
$ cd .. 
$ docker compose up

# import PTC.postman_collection.json into Postman
# File -> Import -> Upload Files under (File tab)
# Run the collection in the other of number given
# Refer the documentation portion of the collection
# to understand more about each endpoints


## System Design ##
Image Processor Service Design
- SpringBoot based microservice
- Simple flow and design as follows

          client ---------(0)----------------+
            ^                                |
            |                                V
            +----(7)--------- HttpRes     HttpReq
                                 ^           | 
                                 |          (1)
                                (6)          |                 
                                 |           V                    
          +--(E1)-- JwtHelper <--|--(2)-- JwtValidationInterceptor    
          |                      |           | (preHandle flow)
          |                      |          (3)
          |                      |           |
          V                      |           V                    
 UnauthorizedException     ImageProcessorController --(4)-> ImageProcessorCoordinator
          |                                                     (ImageProcessorTask)   
          V   (E)                                                   |     |
  ImageProcessorExceptionHandler                                    |     |
          ^                                                         |     |
          |                                                       (5.1) (5.2) submitJob()
          |                                                         |    (5) fetchJobStatus()
  ChecksumNotMatchException <--(E2)----- RequestMessageVerifier <---+    (5) fetchJobResult()
                                                                          |
                                              HttpInvoker <---------------+
                                                   |
                                                   |---> making api call to http://worker.cloud.net
                                                   |
                                                   |---> making api call to http://worker.blob.net
  
  (1) ImageProcessorController Receive http request from client

  (2) ImageProcessorController hand over the processing to ImgProcCord (business layer)

  (3) ImageProcessorCoordinator verifies the authenticity of the request (by inspecting jwt).
      if jwt invalid, throws UnauthorizedException (E1)

  (4) ImgProcCord verifies the json message payload (by inspecting md5 checksum)
      if checksum invalid, throws ChecksumNotMatchException (E2)

  (5) submitJob/featchJob/fetchJobResult methods are used to make backend service api calls
  
  (E) If any exception, handler will handle the exception 
      by logging, or by sending common format error response


## ASSUMPTIONS AND IMPLEMENTATION APPROACH ##
1. Authentication using jwt
- There must be a central IDM server that can allow server and client to connect
  then validate their user/password/roles etc., as part of authentication and 
	authorization flow.
- For this PoC, server/client both are explicitely creating a jwt token by having
  a common secret key. This helps to extchange message between client and server.	
- Not using Spring Security just to keep system design simple.
- Jwt token signature based validation is implemented

2. Jwt integration
OPTION 1: Manually creating JWT string
- define custom secret key through JCA KeyProvider API
- secretKey generated at the startup of spring boot application
- pick the base64 encoded key from application startup log as shown below
  "2021-04-24 22:32:58.027  INFO 11898 --- [  restartedMain] net.cloud.imageprocessor.util.JwtHelper  : generated 
  key: G2AudoHAq2dGTWxzROV/hAJe1ZEY2/O2ef65CWGn5Vk="
 - Go to https://jwt.io
 - Paste the given jwt string on Encode section	
 - Paste the generated key in Decode section and make sure "secret base64 encoded" enabled
 - Copy the updated token from the Encode section
 - Use "X-Jwt-Token: Bearer <update-jwt-string>" header when accessing the endpoints
OPTION 2: Automated way to get JWT string
- /login endpoint provided for getting jwt token (as a non functional requirement)

3. Mocking services
- MockServer used for creating mocks 
   - https://www.mock-server.com
- Mocks created by calling REST API exposed from MockServer library
   - https://www.mock-server.com/mock_server/getting_started.html
- Mocked "worker.blob.net" service provides image from /api/v1/job/1 endpoint
- Mocked "workder.cloud.net" service for submitting image through /api/v1/job endpoint
- Mocked "workder.cloud.net" service provides status from /api/v1/job/2/status
- Two Docker container created for blob and cloud worker service in docker compose.

4. There is no meaningful response from the image-processor api, just returns whatever response received
   from the backend (worker.cloud.net/worker.blob.net api) calls

5. Scalabling of image-processor service can be done by spinning one or more container and add to docker cluster

6. Resilience of image-processor service is achieved by having a robust error handler in place at the code level
   It handles and returns generic error as response, if the upstream services are unreachable, or any other error

7. There is no versioning requirement for the image-processor, so ignored just for simplicity

8. Spring actuator is used for getting healthcheck, metrics, and all other info details about the service

## POSTMAN SCRIPT ##
Importing the PTC.postman_collection.json file, then run the endpoints to see the details.

Details about service as given in the table below
+--------+------------------------------+------------------------------------------------------------------------+
| Method |         Endpoints            |                           Description                                  |
+--------+------------------------------+------------------------------------------------------------------------+
| GET    | localhost:8080/login         | Generates a JWT, and used across all other services                    |
|        |                              |                                                                        |
| POST   | localhost:8080/job           | Submits a job to worker-service, payload strucutred as per requirement |
|        |                              |                                                                        |
| GET    | localhost:8080/job/1         | Retrieve image details as per MIME type image/png based on jobId       |
|        |                              |                                                                        |
| GET    | localhost:8080/job/1/status  | Retireve job status based on the jobId, mocked backend services have   |
| GET    | localhost:8080/job/2/status  |   the following setup. i.e. calling /job/1/status, /job/2/status and   |
| GET    | localhost:8080/job/3/status  |   /job/3/status would return SUCCESS, RUNNING, FAILED respectively     |
+--------+------------------------------+------------------------------------------------------------------------+



## DEBUGING COMMANDS AND INSTRUCTION ##
  used during the development phase of PoC
# A) MOCKING BACKEND SERVICES
# 1. MockServer side
docker run --name workder-service --rm -p 1080:1080 mockserver/mockserver -logLevel INFO -serverPort 1080
(or)
java -jar mockserver-netty-5.11.1-jar-with-dependencies.jar -serverPort 1080,1081 -logLevel INFO

# worker service endpoints
# 2. Inject Mock requests/responses
cat worker-cloud-net.service | bash

# 3. Client side (Debugging purpose Only)
curl -v -X POST localhost:1080/api/v1/job
curl -v -X GET localhost:1080/api/v1/job/1/status
curl -v -X GET localhost:1080/api/v1/job/2/status
curl -v -X GET localhost:1080/api/v1/job/3/status
curl -v -X GET localhost:1080/api/v1/job/1/result --output t.png

# blob service endpoints
# 4. Inject Mock requests/responses
cat worker-blob-net.service | bash

# 5. Client side (Debugging purpose Only)
curl -v -X POST -H 'Content-Type: image/png' --data-binary "@test.png" localhost:1081/api/v1/blob
curl -v -X GET localhost:1081/api/v1/blob/1 -o test.png

# B) IMAGE PROCESSOR SERVICE (SpringBoot)
Postman client used for testing

# C) CLIENT
# curl -v -X POST \
#  -H 'Content-Type: image/png' \
#  -H 'X-Jwt-Token: Bearer <Base64EncodedValue>' \
#  --data-binary "@test.png" localhost:8080/job

(echo -n '{"encoding":"base64","MD5":"<checksum>","content":"`echo hello | base64`"}') | curl -v -H 'content-type: application/json' -H 'X-Jwt-Token: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNjE5MjgwMDQ5LCJhdWQiOiJjb20uY29tcGFueS5qb2JzZXJ2aWNlIiwibmFtZSI6IkpvaG4gRG9lIiwidGlkIjoxLCJvaWQiOjEsImF6cCI6IjEiLCJlbWFpbCI6ImN1c3RvbWVyQG1haWwuY29tIn0.ZWT3ZhWDANMx4TFiQlZjA2hKJkVJvZ1m44r-TxJ-YgQ' -d @- localhost:8080/job


## SCOPE FOR IMPROVEMENTS ##
- Image processor configuration can be externalized from source code. That decouples the configuration the application, 
  easy for managing change with respect to different environment
- SpringBoot fat jar (image-processor-0.0.1-SNAPSHOT.jar) can be optimized using layering tools provided from the spring
  community tools
- Circuit breaker pattern can be added to improve the resiliencey of the application at the integration points.
- Kubernetes replication pattern can be used for deploying our service in kubernetes cluster to improve the availability
  of the image-processor service.
- Scalability of the service can also be improvised by using kubernetes orchestration
