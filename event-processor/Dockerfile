FROM openjdk:17
COPY target/event-processor-1.0.0.jar /app-event-processor/event-processor-1.0.0.jar
WORKDIR /app-event-processor
EXPOSE 8082
CMD ["java", "-jar", "event-processor-1.0.0.jar"]
