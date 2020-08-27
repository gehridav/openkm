FROM openkm/openkm-ce:6.3.9

LABEL maintainer="gehrig.david@gmail.com"

RUN apt-get update && \
    apt-get install -y tesseract-ocr-deu

COPY OpenKM.cfg /opt/tomcat
COPY logback.xml /opt/tomcat
COPY server.xml /opt/tomcat/conf
