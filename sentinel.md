# Sentinel
[控制台参考](https://sentinelguard.io/zh-cn/docs/dashboard.html)

1. data项目启动添加jvm参数 -Dcsp.sentinel.dashboard.server=localhost:8090

java -Dserver.port=8090 -Dcsp.sentinel.dashboard.server=localhost:8090 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.8.jar
