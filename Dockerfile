FROM openjdk:21
VOLUME /tmp
EXPOSE 8080

#Configuracion de la base de datos
ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/prueba_tecnica
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=""

ADD /target/PruebaTecnica-0.0.1-SNAPSHOT.jar spring-boot-docker.jar
LABEL authors="juane"
ENTRYPOINT ["java","-jar","/spring-boot-docker.jar"]