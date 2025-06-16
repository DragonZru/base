
```yaml
services:
  proxy:
    image: apache/rocketmq:5.3.2
    container_name: proxy
    depends_on:
      - broker
      - namesrv
    ports:
      - 8080:8080
      - 8081:8081
    #restart: on-failure
    environment:
      - NAMESRV_ADDR=namesrv:9876
    command: sh mqproxy
  
  namesrv:
    image: apache/rocketmq:5.3.2
    restart: always
    hostname: namesrv
    environment:
      MAX_POSSIBLE_HEAP: 100000000
    ports:
      - 9876:9876
    command: sh mqnamesrv
#docker run -d -p 9876:9876 --name rocketmqNamesrv -e "MAX_POSSIBLE_HEAP=100000000" apache/rocketmq sh mqnamesrv

  broker:
    image: apache/rocketmq:5.3.2
    restart: always
    hostname: broker
    ports:
      - 10909:10909
      - 10911:10911
    environment:
      MAX_POSSIBLE_HEAP: 200000000
      NAMESRV_ADDR: namesrv:9876
#    links: 
#      - namesrv:namesrv
    command: sh mqbroker
#docker run -d -p 10911:10911 -p 10909:10909 --name rocketmqBroker --link rocketmqNamesrv:namesrv -e "NAMESRV_ADDR=namesrv:9876" -e "MAX_POSSIBLE_HEAP=200000000" apache/rocketmq sh mqbroker

  console:
    image: apacherocketmq/rocketmq-dashboard:2.0.1
    restart: always
    hostname: rocketmqDashboard
    ports:
      - 8088:8080
    environment:
      - "JAVA_OPTS=-Drocketmq.namesrv.addr=namesrv:9876" 
#    links: 
#      - namesrv:namesrv
#docker run -d --name rocketmqDashboard --link rocketmqNamesrv:namesrv -e "JAVA_OPTS=-Drocketmq.namesrv.addr=namesrv:9876" -p 8080:8080 -t apacherocketmq/rocketmq-dashboard:latest
```