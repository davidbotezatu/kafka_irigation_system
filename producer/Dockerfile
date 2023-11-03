FROM maven:3.9.5-amazoncorretto-21-debian as builder

WORKDIR /app

COPY ./pom.xml ./pom.xml

RUN mvn clean package -DskipTests

COPY ./src ./src

RUN mvn clean package

FROM amazoncorretto:21.0.1-al2023

WORKDIR /app

COPY --from=builder /app/target/producer-1.0-SNAPSHOT.jar producer.jar

CMD ["java", "-jar", "producer.jar"]