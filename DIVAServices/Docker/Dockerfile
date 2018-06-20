FROM marcovr/python
MAINTAINER marco.vonraumer@unifr.ch
RUN mkdir -p /input/
RUN mkdir -p /output/
RUN apt-get update && apt-get install -y zip

COPY script.sh /input/script.sh
COPY binarize_manager.py /input/binarize_manager.py
COPY graph_converter.py /input/graph_converter.py
COPY graph_manager.py /input/graph_manager.py
COPY run.py /input/run.py

RUN rm -rf /tmp/* /var/tmp/* .git
WORKDIR /input
RUN chmod +x *
CMD ["/input/script.sh"]
