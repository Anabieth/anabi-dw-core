FROM openjdk:8-jre

ENV TZ=Europe/Riga
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG VER=*
ADD ./build/libs/dwh-core-${VER}.jar /opt/app/dwh-core.jar

WORKDIR /opt/app/

ENTRYPOINT java $JAVA_OPTIONS -Dspring.profiles.active=prod -jar dwh-core.jar

EXPOSE 8080
