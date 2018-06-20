FROM anapsix/alpine-java
MAINTAINER marco.vonraumer@unifr.ch
RUN apk update && apk add --no-cache curl zip

RUN mkdir -p /input/
RUN mkdir -p /output/

COPY script.sh /input/script.sh
COPY graphtransformer.jar /input/graphtransformer.jar

RUN rm -rf /tmp/* /var/tmp/* .git /var/cache/apk/*
WORKDIR /input
RUN chmod +x *
CMD ["/input/script.sh"]
