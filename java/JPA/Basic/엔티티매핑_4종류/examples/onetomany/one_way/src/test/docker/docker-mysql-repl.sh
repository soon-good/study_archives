#!/bin/zsh

name_chartnomy_mysql='chartnomy-mariadb'

cnt_chartnomy_mysql=`docker container ls --filter name=chartnomy-mariadb | wc -l`
cnt_chartnomy_mysql=$(($cnt_chartnomy_mysql -1))

if [ $cnt_chartnomy_mysql -eq 0 ]
then
    echo "'$name_chartnomy_mysql' 컨테이너가 없습니다. 컨테이너를 구동해주세요."

else
    echo "'$name_chartnomy_mysql' 컨테이너의 BASH 쉘 접속을 시작합니다."
    docker container exec -it chartnomy-mariadb sh
fi
