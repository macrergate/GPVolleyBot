FROM eclipse-temurin:17
WORKDIR /app
COPY /target/volleybot-1.0.0.jar /app/volleybot.jar
VOLUME /app/data
# ENV SPRING_PROFILES_ACTIVE=development
ENTRYPOINT ["java", "-jar", "volleybot.jar"]
