#FROM openjdk:8-jdk-alpine
FROM szopen/openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=./target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","org.openea.EapFastApplication"]