FROM openjdk:21
VOLUME /tmp
EXPOSE 8080

ADD /target/PruebaTecnica-0.0.1-SNAPSHOT.jar spring-boot-docker.jar
LABEL authors="juane"
ENTRYPOINT ["java","-jar","/spring-boot-docker.jar"]