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
- **docker-compose.yml**
```yaml
#version: '20.10.24'

# MGR 组复制
services:
  MGR1:
    image: mysql:8.0.32
    container_name: mgr1
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
    container_name: mgr2
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
    container_name: mgr3
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
loose-group_replication_local_address=mgr1:33061
loose-group_replication_group_seeds=mgr1:33061,mgr2:33061,mgr3:33061
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
loose-group_replication_local_address=mgr2:33061
loose-group_replication_group_seeds=mgr1:33061,mgr2:33061,mgr3:33061
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
loose-group_replication_local_address=mgr3:33061
loose-group_replication_group_seeds=mgr1:33061,mgr2:33061,mgr3:33061
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