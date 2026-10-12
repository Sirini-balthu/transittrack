# Builds backend + frontend into ONE app (frontend is served by the backend).
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src
COPY frontend frontend
RUN rm -f frontend/server.js frontend/package.json frontend/.gitignore \
 && mkdir -p backend/src/main/resources/static \
 && cp -r frontend/. backend/src/main/resources/static/ \
 && cd backend && mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx380m","-jar","app.jar"]
