# Verwenden Sie ein schlankes OpenJDK-Basisimage
FROM eclipse-temurin:21-alpine

# Setze das Arbeitsverzeichnis
WORKDIR /app

# Kopiere die JAR-Datei und das Datenverzeichnis in das Image
COPY build/libs/xrviz.jar /app/xrviz.jar
COPY data /app/data

# Exponieren Sie die relevanten Ports (optional, falls Ihr daemon auf bestimmten ports hört)
EXPOSE 4000

# Startbefehl für das JAR
CMD ["java", "-jar", "/app/xrviz.jar"]