# ROCKET
## 5.X(Dledger版本)
[docker-compose.yml](./docker-compose/rocketmq/docker-compose-DLedger.yml)

[TODO raft算法]()

## FAQ
1. proxy 启动失败？
> proxy 需要指定proxy.json，默认 rocketMQClusterName = DefaultCluster, proxy启动会去rocketMQClusterName创建指定topic `DefaultHeartBeatSyncerTopic` 
> 所以proxy需要和broker.conf配置保持一致(brokerClusterName)

2. ACL2.0
> 