# docker compose yaml
**network elastic 需要预先创建**
https://www.cnblogs.com/cgsdg/p/16446865.html
```yaml
services:
  elasticsearch:
    image: elasticsearch:8.14.3
    container_name: elasticsearch
    ports:
      - 9200:9200
    deploy:
      resources:
        limits:
          memory: 1GB
    networks:
      - elastic
    
  logstash:
    image: logstash:8.14.3
    container_name: logstash
    ports:
      - 4560:4560
    volumes:
      - ./pipelines.yml:/usr/share/logstash/config/pipelines.yml
      - ./logstash-sb.conf:/usr/share/logstash/config/logstash-sb.conf
      - ./http_ca.crt:/usr/share/logstash/config/certs/http_ca.crt
    depends_on:
      - elasticsearch
    networks:
      - elastic
    
  kibana:
    image: kibana:8.14.3
    container_name: kibana
    environment:
#      - ELASTICSEARCH_URL=http://elasticsearch:9200
      - I18N_LOCALE=zh-CN
    ports:
      - 5601:5601 
    depends_on:
      - elasticsearch
    networks:
      - elastic
      
networks:
  elastic:
    external: true
```
## pipelines.yml
```yaml
# This file is where you define your pipelines. You can define multiple.
# For more information on multiple pipelines, see the documentation:
#   https://www.elastic.co/guide/en/logstash/current/multiple-pipelines.html

- pipeline.id: main
  path.config: "/usr/share/logstash/pipeline"
- pipeline.id: springboot
  path.config: "/usr/share/logstash/config/logstash-sb.conf"
  pipeline.workers: 2

```

## logstash-sb.conf
```
input {
  tcp {
    mode => "server"
    host => "0.0.0.0"
    port => 4560
    codec => json_lines
  }
}
output {
  stdout{
       codec => rubydebug
    }
  elasticsearch {
    hosts => ["https://172.18.0.2:9200/"]
    ssl => true
    cacert => '/usr/share/logstash/config/certs/http_ca.crt'
    user => "elastic"
    password => "Kvnvuc670Cidto0wB_Bo"
    index => "springboot-logstash-%{+YYYY.MM.dd}"
  }
}

```
- step1: 重置密码与token: elasticsearch-reset-password -u elastic & elasticsearch-create-enrollment-token -s kibana
 [参考](https://www.elastic.co/guide/en/kibana/current/docker.html) P.6
- step2: docker cp elasticsearch:/usr/share/elasticsearch/config/certs/http_ca.crt ./http_ca.crt
[reference-1](https://www.elastic.co/guide/en/elasticsearch/reference/8.14/configuring-stack-security.html)
[reference-2](https://www.elastic.co/guide/en/logstash/current/ls-security.html#es-sec-plugin) Configure the elasticsearch output


—————————————————————————————————
# 7X版本
```yaml
services:
  elasticsearch:
    image: elasticsearch:7.7.0
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
#      - logger.level=warn
#      - xpack.security.enrollment.enabled=true
#      - xpack.security.http.ssl.enabled=false
#      - xpack.security.enabled=false
      - http.cors.enabled=true
      - http.cors.allow-origin="*"
#    volumes:
#      - ./data:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    deploy:
      resources:
        limits:
          memory: 1GB
    networks:
      - elastic
    
  logstash:
    image: logstash:7.7.0
    container_name: logstash
    ports:
      - 4560:4560
#    environment:
#      - xpack.monitoring.elasticsearch.username=elastic
#      - xpack.monitoring.elasticsearch.password=yPhxGjZ0qgmll*SDPgPc
    volumes:
      - ./pipelines.yml:/usr/share/logstash/config/pipelines.yml
      - ./logstash-sb.conf:/usr/share/logstash/config/logstash-sb.conf
    depends_on:
      - elasticsearch
    networks:
      - elastic
    
  kibana:
    image: kibana:7.7.0
    container_name: kibana
    environment:
      - ELASTICSEARCH_URL=http://elasticsearch:9200
      - I18N_LOCALE=zh-CN
    ports:
      - 5601:5601 
    depends_on:
      - elasticsearch
    networks:
      - elastic
      
networks:
  elastic:
    external: true

```
```yaml
# v7
input {
  tcp {
    mode => "server"
    host => "0.0.0.0"
    port => 4560
    codec => json_lines
  }
}
output {
  stdout{
       codec => rubydebug
    }
  elasticsearch {
    hosts => ["http://elasticsearch:9200"]
#    user => "elastic"
#    password => "yPhxGjZ0qgmll*SDPgPc"
    index => "springboot-logstash-%{+YYYY.MM.dd}"
  }
}
```