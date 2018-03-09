FROM openkm/openkm

LABEL maintainer="gehrig.david@gmail.com"

RUN apt-get update && \
    apt-get install -y tesseract-ocr-deu && \
    apt-get install -y curl

RUN curl -sL https://10gbps-io.dl.sourceforge.net/project/openkm/6.3.4/OpenKM-6.3.4.zip -o /opt/openkm/OpenKM-6.3.4.zip && \
    unzip -j -o /opt/openkm/OpenKM-6.3.4.zip OpenKM.war -d /opt/openkm/webapps && \
    rm /opt/openkm/OpenKM-6.3.4.zip

COPY OpenKM.cfg /opt/openkm
COPY logback.xml /opt/openkm
COPY server.xml /opt/openkm/conf
