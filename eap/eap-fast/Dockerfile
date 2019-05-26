FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ARG PORT
ENV port ${PORT}
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}","/app.jar"]
