FROM docker.io/library/eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B package -DskipTests && cp target/*.jar app.jar

FROM docker.io/library/eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 spring
COPY --from=build /app/app.jar app.jar
USER spring

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
