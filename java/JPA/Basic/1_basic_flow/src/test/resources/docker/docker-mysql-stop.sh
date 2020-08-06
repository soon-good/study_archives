#!/bin/zsh

name_studyjpa_mysql='studyjpa-mariadb'

cnt_studyjpa_mysql=`docker container ls --filter name=studyjpa-mariadb | wc -l`
cnt_studyjpa_mysql=$(($cnt_studyjpa_mysql -1))

if [ $cnt_studyjpa_mysql -eq 0 ]
then
    echo "'$name_studyjpa_mysql' 컨테이너가 없습니다. 삭제를 진행하지 않습니다."

else
    echo "'$name_studyjpa_mysql' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    docker container stop studyjpa-mariadb
    rm -rf ~/env/docker/studyjpa/volumes/studyjpa-mariadb/*
    echo "\n'$name_studyjpa_mysql' 컨테이너 삭제를 완료했습니다.\n"
fi