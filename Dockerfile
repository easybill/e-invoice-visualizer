FROM eclipse-temurin:21-alpine

RUN adduser -H -D xrviz xrviz
USER xrviz

WORKDIR /app

COPY --chown=xrviz:xrviz build/libs/xrviz.jar /app/xrviz.jar
COPY --chown=xrviz:xrviz data /app/data

EXPOSE 4000

CMD ["java", "-jar", "xrviz.jar"]