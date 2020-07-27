#!/bin/zsh

docker container run \
           --rm -it --name logstash-chartnomy -d \
           -v ~/logstash/chartnomy/pipeline:/usr/share/logstash/pipeline/ \
           -v ~/logstash/chartnomy/config/logstash.yml:/usr/share/logstash/config/logstash.yml \
           -v ~/logstash/chartnomy/config/logstash-sample.conf:/usr/share/config/logstash-sample.conf \
           --link es-chartnomy:elasticsearch \
           --net net-elk-chartnomy \
           docker.elastic.co/logstash/logstash:7.8.0
