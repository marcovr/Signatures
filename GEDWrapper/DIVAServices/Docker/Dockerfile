FROM anapsix/alpine-java
MAINTAINER marco.vonraumer@unifr.ch
RUN apk update && apk add --no-cache curl

RUN mkdir -p /input/
RUN mkdir -p /output/

COPY script.sh /input/script.sh
COPY gedwrapper.jar /input/gedwrapper.jar
COPY GED.jar /input/GED.jar
COPY default.prop /input/default.prop

RUN rm -rf /tmp/* /var/tmp/* .git /var/cache/apk/*
WORKDIR /input
RUN chmod +x *
CMD ["/input/script.sh"]
