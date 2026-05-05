FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY . .
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/petmarket-backend-0.0.1-SNAPSHOT.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
