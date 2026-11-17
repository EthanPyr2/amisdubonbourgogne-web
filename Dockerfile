# ---------- 1. Compilation (JDK + Maven wrapper) ----------
FROM docker.io/library/eclipse-temurin:25-jdk AS build
WORKDIR /app

# Dépendances d'abord : cette étape reste en cache tant que pom.xml ne change pas
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

COPY src src
# Les tests sont lancés à part (certains ont besoin d'une base de données)
RUN ./mvnw -q -B package -DskipTests && cp target/*.jar app.jar

# ---------- 2. Exécution (JRE seul, utilisateur sans privilèges) ----------
FROM docker.io/library/eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 spring
COPY --from=build /app/app.jar app.jar
USER spring

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
