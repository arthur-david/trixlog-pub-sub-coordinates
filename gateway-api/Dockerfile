FROM openjdk:17
COPY target/gateway-api-1.0.0.jar /app-gateway-api/gateway-api-1.0.0.jar
WORKDIR /app-gateway-api
EXPOSE 8081
CMD ["java", "-jar", "gateway-api-1.0.0.jar"]
