#!/bin/zsh
docker container run --rm -d --name kibana-chartnomy --link es-chartnomy:elasticsearch --net net-elk-chartnomy -p 5601:5601 kibana:7.8.0
