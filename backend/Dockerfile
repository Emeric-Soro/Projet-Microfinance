FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

ENV MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"

COPY pom.xml ./

COPY src ./src
RUN mvn -B -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
