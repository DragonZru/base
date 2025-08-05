#/bin/bash

# 获取用户输入的 Docker 网络名称，默认值为 redis_default
read -p "please enter Docker network (default: redis_default): " network_name
network_name=${network_name:-redis_default}

# 检查网络是否存在
if docker network ls | grep -q "$network_name"; then
    read -p "docker network '$network_name' exist. delete ？(y/n): " confirm
    if [[ $confirm == "y" ]]; then
        docker network rm "$network_name"
        echo "delete '$network_name' done."
    fi
fi

docker network create "$network_name"

# 获取第一个非本地回环的 IP 地址
ip_address=$(ifconfig | grep 'inet ' | awk '$2 != "127.0.0.1" {print $2}' | head -n 1) 

# 获取用户输入的 IP 地址,默认值自动获取
read -p "please enter cluster-announce-ip （default "$ip_address"）:" cluster_ip
cluster_ip=${cluster_ip:-$ip_address}
echo "已设置 cluster-announce-ip 为: ${cluster_ip}"


for seq in $(seq 1 6); 
do 
rm ./redis-${seq}.conf
touch ./redis-${seq}.conf
cat  << EOF > ./redis-${seq}.conf

port 637${seq}

bind 0.0.0.0

appendonly yes

protected-mode no

#enable cluster mode
cluster-enabled yes

#ms
cluster-node-timeout 15000

#集群内配置文件
cluster-config-file "nodes-${seq}.conf"

#公网选服务器ip,内网查看ifconfig eh0 ip4地址
cluster-announce-ip ${cluster_ip}
cluster-announce-port 637${seq}
cluster-announce-bus-port 1637${seq}
EOF
done


# 获取创建的网络的 IP 地址
network_ip_prefix=$(docker network inspect "$network_name" -f '{{range .IPAM.Config}}{{.Subnet}}{{end}}' | cut -d '.' -f1-3)

docker compose -f ./redis_cluster.yml up -d

# 自动检测 Redis 容器是否启动
echo "等待 Redis 容器启动..."
while true; do
    # 检查 Redis 是否可以连接，指定端口
    if docker exec node-1 redis-cli -p 6371 ping | grep -q "PONG"; then
        echo "Redis:6371  容器已启动."
        if docker exec node-2 redis-cli -p 6372 ping | grep -q "PONG"; then
            echo "Redis:6372 容器已启动."
            if docker exec node-3 redis-cli -p 6373 ping | grep -q "PONG"; then
                echo "Redis:6373 容器已启动."
                if docker exec node-4 redis-cli -p 6374 ping | grep -q "PONG"; then
                    echo "Redis:6374 容器已启动."
                    if docker exec node-5 redis-cli -p 6375 ping | grep -q "PONG"; then
                        echo "Redis:6375 容器已启动."
                        if docker exec node-6 redis-cli -p 6376 ping | grep -q "PONG"; then
                            echo "Redis:6376 容器已启动."
                            break
                        fi
                    fi
                fi
            fi
        fi
    fi
    sleep 1
done

# 自动执行集群创建命令
echo "正在自动创建 Redis 集群..."
docker exec node-1 redis-cli --cluster create \
  ${network_ip_prefix}.2:6371 \
  ${network_ip_prefix}.3:6372 \
  ${network_ip_prefix}.4:6373 \
  ${network_ip_prefix}.5:6374 \
  ${network_ip_prefix}.6:6375 \
  ${network_ip_prefix}.7:6376 \
  --cluster-replicas 1 \
  --cluster-yes