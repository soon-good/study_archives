#!/bin/zsh

name_studyjpa_mysql='studyjpa-mariadb'
cnt_studyjpa_mysql=`docker container ls --filter name=studyjpa-mariadb | wc -l`
cnt_studyjpa_mysql=$(($cnt_studyjpa_mysql -1))

if [ $cnt_studyjpa_mysql -eq 0 ]
then
    echo "'$name_studyjpa_mysql' 컨테이너를 구동시킵니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/studyjpa/volumes/studyjpa-mariadb
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/studyjpa/volumes/studyjpa-mariadb
    fi

    # mariadb 컨테이너 구동 & 볼륨 마운트
    docker container run --rm -d -p 7306:3306 --name studyjpa-mariadb \
                -v ~/env/docker/studyjpa/volumes/studyjpa-mariadb:/var/lib/mysql \
                -e MYSQL_ROOT_PASSWORD=1111 \
                -e MYSQL_DATABASE=jpa_basic \
                -e MYSQL_USER=testuser \
                -e MYSQL_PASSWORD=1111 \
                -d mariadb:latest \
                --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

else
    echo "'$name_studyjpa_mysql' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    # 컨테이너 중지 & 삭제
    docker container stop studyjpa-mariadb

    # 컨테이너 볼륨 삭제
    rm -rf ~/env/docker/studyjpa/volumes/studyjpa-mariadb/*
    echo "\n'$name_studyjpa_mysql' 컨테이너 삭제를 완료했습니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/studyjpa/volumes/studyjpa-mariadb
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/studyjpa/volumes/studyjpa-mariadb
    fi

    # mariadb 컨테이너 구동 & 볼륨 마운트
    echo "'$name_studyjpa_mysql' 컨테이너를 구동시킵니다."
    docker container run --rm -d -p 7306:3306 --name studyjpa-mariadb \
                -v ~/env/docker/studyjpa/volumes/studyjpa-mariadb:/var/lib/mysql \
                -e MYSQL_ROOT_PASSWORD=1111 \
                -e MYSQL_DATABASE=jpa_basic \
                -e MYSQL_USER=testuser \
                -e MYSQL_PASSWORD=1111 \
                -d mariadb:latest \
                --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
fi
