FROM openjdk:17-jdk-slim

WORKDIR /app

#COPY build/libs/*.jar app.jar
COPY store-app/build/libs/*.jar app.jar

CMD ["java", "-jar", "app.jar"]