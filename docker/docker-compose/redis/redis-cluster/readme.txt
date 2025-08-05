#### 查看 docker network 信息. docker 默认创建 app_default network, 可以 docker network ls 自行查看
```shell
docker network inspect redis-cluster_default
```
进入容器，配置集群(ip:port 与docker网络中对应，--cluster-replicas 1 表示一主一从)
```shell
docker exec -it node-1 /bin/bash 
```
```shell
redis-cli --cluster create 172.23.0.2:6371 172.23.0.3:6372 172.23.0.4:6373 172.23.0.5:6374 172.23.0.6:6375 172.23.0.7:6376 --cluster-replicas 1
```
#### 提升ok 即完成. 可以 redis-cli -p port 进入redis实例：cluster info 查看集群信息，测试 get set k/v   >>   redis-cli -p port -c  （-c 表示以集群模式）