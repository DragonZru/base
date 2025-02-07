## redis-sentinel 示例，docker-compose up -d
**redis.conf & sentinel.conf 默认配置就行，个性化配置自行修改**
**ps: sentinel.conf 所在文件夹 chmod -R 777 否则redis不能修改配置**
```yaml
services:
  redis-master:
    image: redis:latest
    container_name: master-6379
    ports:
      - "6379:6379"
    volumes:
      - /Users/ylli/Downloads/redis/redis.conf:/etc/redis/redis.conf
    command: redis-server /etc/redis/redis.conf --bind 0.0.0.0 --protected-mode no --requirepass 123456

  redis-slave1:
    image: redis:latest
    container_name: slave-8001
    ports:
      - "8001:6379"
    volumes:
      - /Users/ylli/Downloads/redis/redis.conf:/etc/redis/redis.conf
    command: redis-server /etc/redis/redis.conf --replicaof master 6379  --bind 0.0.0.0 --protected-mode no --requirepass 123456 --masterauth 123456
    links:
      - redis-master:master

  redis-slave2:
    image: redis:latest
    container_name: slave-8002
    ports:
      - "8002:6379"
    volumes:
      - /Users/ylli/Downloads/redis/redis.conf:/etc/redis/redis.conf
    command: redis-server /etc/redis/redis.conf --replicaof master 6379 --bind 0.0.0.0 --protected-mode no --requirepass 123456 --masterauth 123456
    links:
      - redis-master:master

  redis-sentinel-master:
    image: redis:latest
    container_name: sentinel-26379
    ports:
      - "26379:26379"
    depends_on:
      - redis-master
    volumes:
      - /Users/ylli/Downloads/redis:/etc/redis
    command: redis-sentinel /etc/redis/sentinel.conf

  redis-sentinel-slave1:
    image: redis:latest
    container_name: sentinel-28001
    ports:
      - "28001:26379"
    depends_on:
      - redis-master
    volumes:
      - /Users/ylli/Downloads/redis:/etc/redis
    command: redis-sentinel /etc/redis/sentinel.conf

  redis-sentinel-slave2:
    image: redis:latest
    container_name: sentinel-28002
    ports:
      - "28002:26379"
    depends_on:
      - redis-master
    volumes:
      - /Users/ylli/Downloads/redis:/etc/redis
    command: redis-sentinel /etc/redis/sentinel.conf
```
**sentinel.conf 配置：**
```text
protected-mode no #保护模式关闭，否则需要bind 指定ip
sentinel auth-pass mymaster 123456 
```

-------------------------------------------------------------------------------------

# redis-cluster 示例：
```yaml
services:
  redis-cluster-1:
    image: redis:latest
    container_name: node-1
    ports:
      - "6371:6371"
      - "16371:16371"
    volumes:
      - ./redis-1.conf:/etc/redis/redis.conf
    command: sh -c "redis-server /etc/redis/redis.conf"

  redis-cluster-2:
    image: redis:latest
    container_name: node-2
    ports:
      - "6372:6372"
      - "16372:16372"
    volumes:
      - ./redis-2.conf:/etc/redis/redis.conf
    command: sh -c "redis-server /etc/redis/redis.conf"
    depends_on:
      - redis-cluster-1

  redis-cluster-3:
    image: redis:latest
    container_name: node-3
    ports:
      - "6373:6373"
      - "16373:16373"
    volumes:
      - ./redis-3.conf:/etc/redis/redis.conf
    command: sh -c "redis-server /etc/redis/redis.conf"
    depends_on:
      - redis-cluster-2

  redis-cluster-4:
    image: redis:latest
    container_name: node-4
    ports:
      - "6374:6374"
      - "16374:16374"
    volumes:
      - ./redis-4.conf:/etc/redis/redis.conf
    command: sh -c "redis-server /etc/redis/redis.conf"
    depends_on:
      - redis-cluster-3

  redis-cluster-5:
    image: redis:latest
    container_name: node-5
    ports:
      - "6375:6375"
      - "16375:16375"
    volumes:
      - ./redis-5.conf:/etc/redis/redis.conf
    command: sh -c "redis-server /etc/redis/redis.conf"
    depends_on:
      - redis-cluster-4

  redis-cluster-6:
    image: redis:latest
    container_name: node-6
    ports:
      - "6376:6376"
      - "16376:16376"
    volumes:
      - ./redis-6.conf:/etc/redis/redis.conf
    command: sh -c "redis-server /etc/redis/redis.conf"
    depends_on:
      - redis-cluster-5

```
**redis-x.conf 如下，/path/to/redis-x.conf 自行修改, chmod 777 redis-cluster.sh**
```shell
mkdir -p /Users/ylli/Downloads/redis-cluster
for port in $(seq 6380 6385); 
do 
touch /Users/ylli/Downloads/redis-cluster/redis-${port}.conf
cat  << EOF > /Users/ylli/Downloads/redis-cluster/redis-${port}.conf
port ${port}

#requirepass 1234

bind 0.0.0.0

protected-mode no

#enable cluster mode
cluster-enabled yes

#ms
cluster-node-timeout 15000

#集群内配置文件
cluster-config-file "nodes-${port}.conf"

#公网选服务器ip,内网查看ifconfig eh0 ip4地址
cluster-announce-ip 192.168.10.3
cluster-announce-port ${port}
cluster-announce-bus-port 1${port}
EOF
done
```
**查看 docker network 信息. docker 默认创建 app_default network, 可以 docker network ls 自行查看**
```shell
docker network inspect redis-cluster_default
```
**进入容器，配置集群(ip:port 与docker网络中对应，--cluster-replicas 1 表示一主一从)**
```shell
docker exec -it node-1 /bin/bash 
```
```shell
redis-cli --cluster create 172.20.0.2:6371 172.20.0.3:6372 172.20.0.4:6373 172.20.0.5:6374 172.20.0.6:6375 172.20.0.7:6376 --cluster-replicas 1
```
**提升ok 即完成. 可以 redis-cli -p port 进入redis实例：cluster info 查看集群信息，测试 get set k/v   >>   redis-cli -p port -c  （-c 表示以集群模式**