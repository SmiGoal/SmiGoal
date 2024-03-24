FROM openjdk:17-bullseye

ARG JAR_FILE=build/libs/server-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} ./app.jar

RUN apt-get update && apt-get install -y \
  libnss3 \
  libgconf-2-4 \
  libfontconfig1

ENTRYPOINT ["java", "-jar", "./app.jar"]
