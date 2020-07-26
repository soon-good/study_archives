# docker로 ELK 모두 설치해보기

# 1. docker network

## docker network 생성

net-es-chartnomy 라는 이름의 docker network를 생성한다.

```bash
$ docker network create net-elk-chartnomy
```

## docker network 삭제

```bash
$ docker network rm net-elk-chartnomy
```



# 2. docker ElasticSearch 설치/구동

```bash
$ docker container run --rm -d --name es-chartnomy --net net-elk-chartnomy -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.8.0
```



## ElasticSearch 컨테이너 조회

```bash
$ docker container ls --filter name=es-chartnomy
```



## ElasticSearch 컨테이너 삭제

```bash
$ docker container stop es-chartnomy
```



## ElasticSearch 컨테이너 Bash 접속

```bash
$ docker container exec -it es-chartnomy sh
```



# 3. docker Kibana 설치/구동

```bash
$ docker container run --rm -d --name kibana-chartnomy --link es-chartnomy:elasticsearch --net net-elk-chartnomy -p 5601:5601 kibana:7.8.0
```



## Kibana 컨테이너 조회

```bash
$ docker container ls --filter name=kibana-chartnomy

# ...
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
fa6e6cfd1f6f        kibana:7.8.0        "/usr/local/bin/dumb…"   7 minutes ago       Up 7 minutes        0.0.0.0:5601->5601/tcp   kibana-chartnomy
```



## Kibana 컨테이너 삭제

```bash
$ docker container stop kibana-chartnomy
```



## Kibana 컨테이너 Bash 접속

```bash
$ docker container exec -it kibana-chartnomy sh
```





# 4. docker Logstash 설치/구동

```bash
$ docker container run --rm -it --name logstash-chartnomy -d -v ~/logstash/chartnomy/pipeline:/usr/share/logstash/pipeline/ -v ~/logstash/chartnomy/config/logstash.yml:/usr/share/logstash/config/logstash.yml -v ~/logstash/chartnomy/config/logstash-sample.conf:/usr/share/config/logstash-sample.conf docker.elastic.co/logstash/logstash:7.8.0

# 출력결과 
af87d38750cbcbd7a6ede19cb42d4d3c8377bd15b925d6230fbbd3fd8bb55f85
```



## logstash container 조회

```bash
$ docker container ls --filter name=logstash-chartnomy
```



## logstash container 쉘 접속

```bash
$ docker container exec -it logstash-chartnomy sh
```



## logstash container 삭제

```bash
$ docker container stop logstash-chartnomy
```



## config/logstash.yml

```yaml
http.host: "0.0.0.0"
xpack.monitoring.elasticsearch.hosts: [ "http://elasticsearch:9200" ]
```




## config/logstash-sample.conf

```bash
# Sample Logstash configuration for creating a simple
# Beats -> Logstash -> Elasticsearch pipeline.

input {
  beats {
    port => 5044
  }
}

output {
  elasticsearch {
    hosts => ["http://localhost:9200"]
    index => "%{[@metadata][beat]}-%{[@metadata][version]}-%{+YYYY.MM.dd}"
    #user => "elastic"
    #password => "changeme"
  }
}
```



## config/pipelines.yml

```yaml
# This file is where you define your pipelines. You can define multiple.
# For more information on multiple pipelines, see the documentation:
#   https://www.elastic.co/guide/en/logstash/current/multiple-pipelines.html

- pipeline.id: main
  path.config: "/usr/share/logstash/pipeline"
```



## config/startup.options

```text
################################################################################
# These settings are ONLY used by $LS_HOME/bin/system-install to create a custom
# startup script for Logstash and is not used by Logstash itself. It should
# automagically use the init system (systemd, upstart, sysv, etc.) that your
# Linux distribution uses.
#
# After changing anything here, you need to re-run $LS_HOME/bin/system-install
# as root to push the changes to the init script.
################################################################################

# Override Java location
#JAVACMD=/usr/bin/java

# Set a home directory
LS_HOME=/usr/share/logstash

# logstash settings directory, the path which contains logstash.yml
LS_SETTINGS_DIR=/etc/logstash

# Arguments to pass to logstash
LS_OPTS="--path.settings ${LS_SETTINGS_DIR}"

# Arguments to pass to java
LS_JAVA_OPTS=""

# pidfiles aren't used the same way for upstart and systemd; this is for sysv users.
LS_PIDFILE=/var/run/logstash.pid

# user and group id to be invoked as
LS_USER=logstash
LS_GROUP=logstash

# Enable GC logging by uncommenting the appropriate lines in the GC logging
# section in jvm.options
LS_GC_LOG_FILE=/var/log/logstash/gc.log

# Open file limit
LS_OPEN_FILES=16384

# Nice level
LS_NICE=19

# Change these to have the init script named and described differently
# This is useful when running multiple instances of Logstash on the same
# physical box or vm
SERVICE_NAME="logstash"
SERVICE_DESCRIPTION="logstash"

# If you need to run a command or script before launching Logstash, put it
# between the lines beginning with `read` and `EOM`, and uncomment those lines.
###
## read -r -d '' PRESTART << EOM
## EOM
```



## config/jvm.options

```text
## JVM configuration

# Xms represents the initial size of total heap space
# Xmx represents the maximum size of total heap space

-Xms1g
-Xmx1g

################################################################
## Expert settings
################################################################
##
## All settings below this section are considered
## expert settings. Don't tamper with them unless
## you understand what you are doing
##
################################################################

## GC configuration
-XX:+UseConcMarkSweepGC
-XX:CMSInitiatingOccupancyFraction=75
-XX:+UseCMSInitiatingOccupancyOnly

## Locale
# Set the locale language
#-Duser.language=en

# Set the locale country
#-Duser.country=US

# Set the locale variant, if any
#-Duser.variant=

## basic

# set the I/O temp directory
#-Djava.io.tmpdir=$HOME

# set to headless, just in case
-Djava.awt.headless=true

# ensure UTF-8 encoding by default (e.g. filenames)
-Dfile.encoding=UTF-8

# use our provided JNA always versus the system one
#-Djna.nosys=true

# Turn on JRuby invokedynamic
-Djruby.compile.invokedynamic=true
# Force Compilation
-Djruby.jit.threshold=0
# Make sure joni regexp interruptability is enabled
-Djruby.regexp.interruptible=true

## heap dumps

# generate a heap dump when an allocation from the Java heap fails
# heap dumps are created in the working directory of the JVM
-XX:+HeapDumpOnOutOfMemoryError

# specify an alternative path for heap dumps
# ensure the directory exists and has sufficient space
#-XX:HeapDumpPath=${LOGSTASH_HOME}/heapdump.hprof

## GC logging
#-XX:+PrintGCDetails
#-XX:+PrintGCTimeStamps
#-XX:+PrintGCDateStamps
#-XX:+PrintClassHistogram
#-XX:+PrintTenuringDistribution
#-XX:+PrintGCApplicationStoppedTime

# log GC status to a file with time stamps
# ensure the directory exists
#-Xloggc:${LS_GC_LOG_FILE}

# Entropy source for randomness
-Djava.security.egd=file:/dev/urandom

# Copy the logging context from parent threads to children
-Dlog4j2.isThreadContextMapInheritable=true
```





## log4j2.properties

```text
status = error
name = LogstashPropertiesConfig

appender.console.type = Console
appender.console.name = plain_console
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = [%d{ISO8601}][%-5p][%-25c]%notEmpty{[%X{pipeline.id}]}%notEmpty{[%X{plugin.id}]} %m%n

appender.json_console.type = Console
appender.json_console.name = json_console
appender.json_console.layout.type = JSONLayout
appender.json_console.layout.compact = true
appender.json_console.layout.eventEol = true

rootLogger.level = ${sys:ls.log.level}
rootLogger.appenderRef.console.ref = ${sys:ls.log.format}_console
```

