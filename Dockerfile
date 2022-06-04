FROM openjdk:11-jre-slim
LABEL maintainer="mathew.kapkiai@safaricom.et"
EXPOSE 8085
VOLUME /tmp
ADD target/sms-sender-0.0.1.jar sms-sender-0.0.1.jar
RUN /bin/sh -c 'touch /sms-sender-0.0.1.jar'
ENV TZ=Africa/Nairobi
ENTRYPOINT ["java","-Xmx256m", "-XX:+UseG1GC", "-Djava.security.egd=file:/dev/./urandom","-jar","/sms-sender-0.0.1.jar"]