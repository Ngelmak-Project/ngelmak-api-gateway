FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /ngelmak

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /ngelmak

COPY --from=builder ./ngelmak/target/ngelmak-api-gateway-0.0.1-SNAPSHOT.jar ./ngelmak-api-gateway.jar

EXPOSE 4000

ENTRYPOINT ["java", "-jar", "ngelmak-api-gateway.jar"]