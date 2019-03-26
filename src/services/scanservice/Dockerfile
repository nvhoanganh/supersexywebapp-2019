FROM openjdk:11-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY target/scanservice-0.0.1-1.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]