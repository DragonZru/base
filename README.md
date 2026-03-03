- java17
- spring boot 3.3.0
- mysql,mybatis

## db 管理工具 flyway 
[spring data migration properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.data-migration)

## Gson
[Gson User Guide](https://github.com/google/gson/blob/main/UserGuide.md)

## maven 私服
[nexus](https://hub.docker.com/r/sonatype/nexus3?uuid=F523A7E2-1684-416A-AED6-EEF3021A7F49)
```yaml
services:
  nexus:
    image: sonatype/nexus3:latest
    ports:
      - 8081:8081
    volumes:
      - ./data:/nexus-data
```
初始密码: cat nexus-data/admin.password

## mybatis mapper 
新版mapper5，可以参考mapper4 https://github.com/abel533/Mapper/wiki
[mybatis mapper](https://mapper.mybatis.io/docs/v2.x/1.getting-started.html#_1-1-%E4%B8%BB%E8%A6%81%E7%9B%AE%E6%A0%87)

TODO 性能测试PTS.

// TODO mysql innodb_buffer_pool_size / innodb_buffer_pool_instances / SHOW ENGINE INNODB STATUS\G 持续插入数据观察内存增长与对应指标的变动 / binlog / undolog / relylog 插入到es中


[一次「找回」TraceId的问题分析与过程思考](https://tech.meituan.com/2023/04/20/traceid-google-dapper-mtrace.html)

[日志导致线程Block的这些坑，你不得不防](https://tech.meituan.com/2022/07/29/tips-for-avoiding-log-blocking-threads.html)

[Java线程池实现原理及其在美团业务中的实践](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html)

[设计模式在外卖营销业务中的实践](https://tech.meituan.com/2020/03/19/design-pattern-practice-in-marketing.html)

[从ReentrantLock的实现看AQS的原理及应用](https://tech.meituan.com/2019/12/05/aqs-theory-and-apply.html)

[Java魔法类：Unsafe应用解析](https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html)

[智能支付稳定性测试实战](https://tech.meituan.com/2018/12/13/smart-payment.html)

[不可不说的Java“锁”事](https://tech.meituan.com/2018/11/15/java-lock.html)

[MySQL索引原理及慢查询优化](https://tech.meituan.com/2014/06/30/mysql-index.html)

# TODO 内存回收
- [个人笔记](https://www.processon.com/mindmap/66f1a103ce5f3001cf4b4073)
- [ZGC-介绍1](https://www.baeldung.com/jvm-zgc-garbage-collector)
- [ZGC-JDK](https://wiki.openjdk.org/spaces/zgc/overview)
- [ZGC-jdk](https://wiki.openjdk.org/spaces/zgc/pages/75956367/Pointer+Metadata+using+Multi-Mapped+memory)
- [JEP 439: Generational ZGC](https://openjdk.org/jeps/439)
- [JEP 333：ZGC：一种可扩展的低延迟垃圾回收器（实验性）](https://openjdk.org/jeps/333)
- [新一代垃圾回收器ZGC的探索与实践](https://tech.meituan.com/2020/08/06/new-zgc-practice-in-meituan.html)
- [从实际案例聊聊Java应用的GC优化](https://tech.meituan.com/2017/12/29/jvm-optimize.html)
- [Java Hotspot G1 GC的一些关键技术](https://tech.meituan.com/2016/09/23/g1.html)
- [Java中9种常见的CMS GC问题分析与解决](https://tech.meituan.com/2020/11/12/java-9-cms-gc.html)
- [JDK高版本特性总结与ZGC实践](https://tech.meituan.com/2025/06/20/jdk17-zgc.html)
- [GC - Java 垃圾回收器之G1详解](https://pdai.tech/md/java/jvm/java-jvm-gc-g1.html)
- [b站视频](https://www.bilibili.com/video/BV13J411g7A1/?spm_id_from=333.999.0.0&vd_source=01e2d151bddb473b19388eac938a1a92)
- [简书](https://www.jianshu.com/p/870abddaba41)
- [知乎](https://zhuanlan.zhihu.com/p/52841787)
- [JavaGuide-JVM垃圾回收详解](https://javaguide.cn/java/jvm/jvm-garbage-collection.html#g1-%E6%94%B6%E9%9B%86%E5%99%A8)
- []

# linux 内存回收
- [github](https://github.com/0voice/kernel_memory_management/blob/main/%E2%9C%8D%20%E6%96%87%E7%AB%A0/%E6%B7%B1%E5%85%A5%E7%90%86%E8%A7%A3%20glibc%20malloc%EF%BC%9A%E5%86%85%E5%AD%98%E5%88%86%E9%85%8D%E5%99%A8%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86.md)






































# java内存回收 Garbage Collection GC
> 在学习Java对象的自动内存回收（GC）机制之前，建议先了解JVM内存结构与对象内存分配机制。

https://www.processon.com/view/6284c66b0791290711949fdb
https://pdai.tech/md/java/jvm/java-jvm-struct.html

## jvm内存结构 RuntimeDataArea
## HotSpot对象内存布局 HotSpotObjectMemoryLayout

# java内存模型(JMM) Java Memory Model
> Java语言规范定义的多线程内存访问模型，用于保证共享变量的**可见性**、**原子性**和**有序性**，与垃圾回收无关