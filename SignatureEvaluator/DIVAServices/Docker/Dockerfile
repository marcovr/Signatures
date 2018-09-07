FROM python
MAINTAINER marco.vonraumer@unifr.ch
RUN mkdir -p /input/
RUN mkdir -p /output/
RUN apt-get update && apt-get install -y zip

COPY script.sh /input/script.sh
COPY evaluation.py /input/evaluation.py
COPY plot.py /input/plot.py
COPY run.py /input/run.py

RUN rm -rf /tmp/* /var/tmp/* .git
WORKDIR /input
RUN chmod -R 777 /input/
CMD ["/input/script.sh"]
