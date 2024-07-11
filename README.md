- java17
- spring boot 3.3.0
- mysql,mybatis

## db 管理工具 flyway 
[spring data migration properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.data-migration)

## Gson
[Gson User Guide](https://github.com/google/gson/blob/main/UserGuide.md)

## maven 私服
[nexus](https://hub.docker.com/r/sonatype/nexus3?uuid=F523A7E2-1684-416A-AED6-EEF3021A7F49)
初始密码: cat nexus-data/admin.password

## mybatis mapper 
新版mapper5，可以参考mapper4 https://github.com/abel533/Mapper/wiki
[mybatis mapper](https://mapper.mybatis.io/docs/v2.x/1.getting-started.html#_1-1-%E4%B8%BB%E8%A6%81%E7%9B%AE%E6%A0%87)

## mysql MGR

- **创建network**
```angular17html
docker network create mysql_mgr  
docker inspect mysql_mgr  
--> "Config": [
#                {
#                    "Subnet": "192.168.32.0/20",
#                    "Gateway": "192.168.32.1"
#                }
#            ]

```
- **dockerCompose.yml**
```yaml
#version: '20.10.24'

# MGR 组复制
services:
  MGR1:
    image: mysql:8.0.32
    ports:
      - 13306:3306
    volumes:
      - ./node1.cnf:/etc/mysql/conf.d/my.cnf
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      TZ: Asia/Shanghai
    networks:
      - mysql_mgr

  MGR2:
    image: mysql:8.0.32
    ports:
      - 13307:3306
    volumes:
      - ./node2.cnf:/etc/mysql/conf.d/my.cnf
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      TZ: Asia/Shanghai
    depends_on:
      - MGR1
    networks:
      - mysql_mgr
      
  MGR3:
    image: mysql:8.0.32
    ports:
      - 13308:3306      
    volumes:
      - ./node3.cnf:/etc/mysql/conf.d/my.cnf
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      TZ: Asia/Shanghai
    depends_on:
      - MGR2
    networks:
      - mysql_mgr
      
networks:
  mysql_mgr:
    external: true
```
- **node.cnf**  
chmod 0444 nodex.cnf [Warning: World-writable config file is ignored](https://stackoverflow.com/questions/53741107/mysql-in-docker-on-ubuntu-warning-world-writable-config-file-is-ignored)
```
#node1.cnf
[mysqld]
disabled_storage_engines="MyISAM,BLACKHOLE,FEDERATED,ARCHIVE,MEMORY"

server_id=1
gtid_mode=ON
enforce_gtid_consistency=ON
binlog_checksum=NONE

log_bin=binlog
log_slave_updates=ON
binlog_format=ROW
master_info_repository=TABLE
relay_log_info_repository=TABLE
transaction_write_set_extraction=XXHASH64

plugin_load_add='group_replication.so'
loose-group_replication_group_name="aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
loose-group_replication_start_on_boot=OFF
loose-group_replication_local_address=192.168.32.2:33061
loose-group_replication_group_seeds=192.168.32.2:33061,192.168.32.3:33061,192.168.32.4:33061
loose-group_replication_bootstrap_group=OFF
loose-group_replication_recovery_get_public_key=TRUE
# 是否开启单主模式
loose-group_replication_single_primary_mode=OFF

#report_host=127.0.0.1

# 1-每次事物提交时立即将日志缓冲区刷新到磁盘；0-事物提交时只会写入日志，刷盘由系统决定；2-事物提交时写入日志，刷盘/s 系统
innodb_flush_log_at_trx_commit=1
binlog_expire_logs_seconds=86400

#node2.cnf
[mysqld]
disabled_storage_engines="MyISAM,BLACKHOLE,FEDERATED,ARCHIVE,MEMORY"

server_id=2
gtid_mode=ON
enforce_gtid_consistency=ON
binlog_checksum=NONE

log_bin=binlog
log_slave_updates=ON
binlog_format=ROW
master_info_repository=TABLE
relay_log_info_repository=TABLE
transaction_write_set_extraction=XXHASH64

plugin_load_add='group_replication.so'
loose-group_replication_group_name="aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
loose-group_replication_start_on_boot=OFF
loose-group_replication_local_address=192.168.32.3:33061
loose-group_replication_group_seeds=192.168.32.2:33061,192.168.32.3:33061,192.168.32.4:33061
loose-group_replication_bootstrap_group=OFF
loose-group_replication_recovery_get_public_key=TRUE
loose-group_replication_single_primary_mode=OFF

#report_host=127.0.0.1

innodb_flush_log_at_trx_commit=1
binlog_expire_logs_seconds=86400

#node3.cnf
[mysqld]
disabled_storage_engines="MyISAM,BLACKHOLE,FEDERATED,ARCHIVE,MEMORY"

server_id=3
gtid_mode=ON
enforce_gtid_consistency=ON
binlog_checksum=NONE

log_bin=binlog
log_slave_updates=ON
binlog_format=ROW
master_info_repository=TABLE
relay_log_info_repository=TABLE
transaction_write_set_extraction=XXHASH64

plugin_load_add='group_replication.so'
loose-group_replication_group_name="aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
loose-group_replication_start_on_boot=OFF
loose-group_replication_local_address=192.168.32.4:33061
loose-group_replication_group_seeds=192.168.32.2:33061,192.168.32.3:33061,192.168.32.4:33061
loose-group_replication_bootstrap_group=OFF
loose-group_replication_recovery_get_public_key=TRUE
loose-group_replication_single_primary_mode=OFF

#report_host=127.0.0.1

innodb_flush_log_at_trx_commit=1
binlog_expire_logs_seconds=86400
```
- **repl.sql**
```sql
/*
each node
*/
set SQL_LOG_BIN=0;
CREATE USER repl@'%' IDENTIFIED BY '123456';
GRANT REPLICATION SLAVE ON *.* TO repl@'%';
GRANT CONNECTION_ADMIN ON *.* TO repl@'%';
GRANT BACKUP_ADMIN ON *.* TO repl@'%';
GRANT GROUP_REPLICATION_STREAM ON *.* TO repl@'%';
FLUSH PRIVILEGES;
SET SQL_LOG_BIN=1;
CHANGE MASTER TO MASTER_USER='repl', MASTER_PASSWORD='123456' FOR CHANNEL 'group_replication_recovery';

/*
first node to bootstrap , single primary will be master
*/
SET GLOBAL group_replication_bootstrap_group=ON;
START GROUP_REPLICATION;
SET GLOBAL group_replication_bootstrap_group=OFF;
SELECT * FROM performance_schema.replication_group_members;

/*

*/
-- 在后续节点执行
RESET MASTER;
SET GLOBAL group_replication_recovery_get_public_key=ON;
START GROUP_REPLICATION;

```
## MYSQL SEMI 半同步复制  
- **dockerCompose.yml**
```yaml
#version: '20.10.24'

# semi sync 复制
services:
  salve1:
    image: mysql:8.0.32
    command: mysqld --server-id=101 --log-bin=slave-bin --binlog-format=row --innodb_flush_log_at_trx_commit=1 --relay-log=slave-relay-bin --read-only=1 --gtid_mode=on --enforce_gtid_consistency=on --expire_logs_days=7
    ports:
      - 23306:3306
    environment:
      MYSQL_ROOT_PASSWORD: 123456
    networks:
      - mysql_mgr

  salve2:
    image: mysql:8.0.32
    command: mysqld --server-id=102 --log-bin=slave-bin --binlog-format=row --innodb_flush_log_at_trx_commit=1 --relay-log=slave-relay-bin --read-only=1 --gtid_mode=on --enforce_gtid_consistency=on --expire_logs_days=7
    ports:
      - 23307:3306
    environment:
      MYSQL_ROOT_PASSWORD: 123456
    networks:
      - mysql_mgr

networks:
  mysql_mgr:
    external: true
```
- **repl.sql**
```sql
# 半同步复制
# 主库 see mgr. 授权账户 see mgr readme.


stop slave;
change master to master_host='192.168.32.2',master_user='repl',master_password='123456',master_auto_position=1,GET_MASTER_PUBLIC_KEY=1;
start slave;
show slave status;

```

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

-----------------