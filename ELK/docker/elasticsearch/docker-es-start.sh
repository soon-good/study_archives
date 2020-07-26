#!/bin/zsh
docker container run --rm -d --name es-chartnomy --net net-elk-chartnomy -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.8.0
