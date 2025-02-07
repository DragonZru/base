#/bin/bash

# -------------------------------------------------------------------------
# 创建默认网络
# 获取用户输入的 Docker 网络名称，默认值为 mysql_default
read -p "please enter Docker network (default: mysql_default): " network_name
network_name=${network_name:-mysql_default}
echo "network: $network_name"

# 检查网络是否存在
if docker network ls | grep -q "$network_name"; then
    read -p "docker network '$network_name' exist. Do you want to recreate it ？(y/n): " confirm
    if [[ $confirm == "y" ]]; then
        docker network rm "$network_name"
        echo "docker delete network '$network_name' success."
    fi
fi

if ! docker network ls | grep -q "$network_name"; then
    docker network create "$network_name"
    echo "docker create network '$network_name' success."
fi

# -------------------------------------------------------------------------
# 设置集群数量，默认3
read -p "please enter mysql mgr number (default: 3): " cluster_number
cluster_number=${cluster_number:-3}
echo "number: $cluster_number"

# -------------------------------------------------------------------------
# 设置container_name，用于集群中通信
read -p "please enter container name (default: mgr): " container_name
container_name=${container_name:-mgr}
echo "container name: $container_name"

# -------------------------------------------------------------------------
# 创建配置文件
for seq in $(seq 1 $cluster_number);
do
cat << EOF > ./node${seq}.cnf
[mysqld]
disabled_storage_engines="MyISAM,BLACKHOLE,FEDERATED,ARCHIVE,MEMORY"

server_id=${seq}
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
loose-group_replication_local_address=${container_name}${seq}:33061
loose-group_replication_group_seeds=${container_name}1:33061,${container_name}2:33061,${container_name}3:33061
loose-group_replication_bootstrap_group=OFF
loose-group_replication_recovery_get_public_key=TRUE
# 是否开启单主模式
loose-group_replication_single_primary_mode=OFF

#report_host=127.0.0.1

# 1-每次事物提交时立即将日志缓冲区刷新到磁盘；0-事物提交时只会写入日志，刷盘由系统决定；2-事物提交时写入日志，刷盘/s 系统
innodb_flush_log_at_trx_commit=1
# binlog过期时间 = 15x86400
binlog_expire_logs_seconds=1296000
EOF
# [Warning: World-writable config file is ignored](https://stackoverflow.com/questions/53741107/mysql-in-docker-on-ubuntu-warning-world-writable-config-file-is-ignored)
chmod 0444 ./node${seq}.cnf
done

## -----------------------------------------------------------------------------
# 创建docker-compose.yml
cat << EOF > ./mgr.yml
# MGR 组复制
services:
$(for seq in $(seq 1 ${cluster_number});do
port=$((13305 + seq))
cat << INNER
  MGR${seq}:
    image: mysql:8.0.32
    container_name: ${container_name}${seq}
    ports:
      - ${port}:3306
    volumes:
      - ./node${seq}.cnf:/etc/mysql/conf.d/my.cnf
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      TZ: Asia/Shanghai
    networks:
      - $network_name
$(if [ $seq -gt 1 ]; then
echo "    depends_on:
      - MGR$((seq-1))"; fi)

INNER
done)
networks:
  $network_name:
    external: true
EOF

## ---------------------------------------------------------------------
docker compose -f ./mgr.yml up -d

for seq in $(seq 1 $cluster_number);
do
    # 等待容器启动完成
    for i in {30..0}; do
        if docker exec ${container_name}${seq} mysql -uroot -p123456 -e "SELECT 1;" > /dev/null 2>&1; then
            echo "${container_name}${seq} is up"
            break
        fi
        echo "Waiting for MySQL... ($i seconds left)"
        sleep 1
    done

    docker exec ${container_name}${seq} mysql -uroot -p123456 -e "set SQL_LOG_BIN=0;
    CREATE USER repl@'%' IDENTIFIED BY '123456';
    GRANT REPLICATION SLAVE ON *.* TO repl@'%';
    GRANT CONNECTION_ADMIN ON *.* TO repl@'%';
    GRANT BACKUP_ADMIN ON *.* TO repl@'%';
    GRANT GROUP_REPLICATION_STREAM ON *.* TO repl@'%';
    FLUSH PRIVILEGES;
    SET SQL_LOG_BIN=1;
    CHANGE MASTER TO MASTER_USER='repl', MASTER_PASSWORD='123456' FOR CHANNEL 'group_replication_recovery';"

    if [ "$seq" -eq 1 ]; then
        docker exec ${container_name}${seq} mysql -uroot -p123456 -e "SET GLOBAL group_replication_bootstrap_group=ON;
        START GROUP_REPLICATION;
        SET GLOBAL group_replication_bootstrap_group=OFF;
        SELECT * FROM performance_schema.replication_group_members;"
    else
        docker exec ${container_name}${seq} mysql -uroot -p123456 -e "RESET MASTER;
        SET GLOBAL group_replication_recovery_get_public_key=ON;
        START GROUP_REPLICATION;
        SELECT * FROM performance_schema.replication_group_members;"
    fi
done
