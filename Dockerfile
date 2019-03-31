FROM openjdk:latest

ADD ./target/multi-sec.jar /app/

EXPOSE 8080

CMD [ "java", "-jar",  "/app/multi-sec.jar"]