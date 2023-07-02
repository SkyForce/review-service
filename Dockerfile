FROM eclipse-temurin:17-jre-focal

WORKDIR /review-service
ARG JAR_PATH
COPY ${JAR_PATH} review-service.jar

ENTRYPOINT ["java", "-jar", "/review-service/review-service.jar"]