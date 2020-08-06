#!/bin/zsh

name_studyjpa_mysql='studyjpa-mariadb'

cnt_studyjpa_mysql=`docker container ls --filter name=studyjpa-mariadb | wc -l`
cnt_studyjpa_mysql=$(($cnt_studyjpa_mysql -1))

if [ $cnt_studyjpa_mysql -eq 0 ]
then
    echo "'$name_studyjpa_mysql' 컨테이너가 없습니다. 컨테이너를 구동해주세요."

else
    echo "'$name_studyjpa_mysql' 컨테이너의 BASH 쉘 접속을 시작합니다."
    docker container exec -it studyjpa-mariadb sh
fi
