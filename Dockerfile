FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY build/libs/xrviz.jar /app/xrviz.jar
COPY data /app/data

EXPOSE 4000

CMD ["java", "-jar", "/app/xrviz.jar"]