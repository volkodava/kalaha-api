FROM openjdk:11-jre
ENV PORT "${PORT:-8080}"
VOLUME /tmp
COPY build/libs/*.jar app.jar
CMD [ "bash", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
