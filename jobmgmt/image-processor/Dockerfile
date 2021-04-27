FROM openjdk:8-jdk-alpine
EXPOSE 8080
COPY target/image-processor-0.0.1-SNAPSHOT.jar image-processor-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/image-processor-0.0.1-SNAPSHOT.jar"]