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

## jvm内存结构 RuntimeDataArea
线程私有：
* 程序计数器 ： 记录方法执行行号
* 虚拟机栈(java 虚拟机栈Java Virtual Machine Stacks) ：内部保存一个个栈帧Stack Frame(对应一个个java方法)
  * 栈帧的内部结构：
    * 局部变量表：存储方法参数和局部变量，包括基本数据类型（int、long 等）和对象引用（reference）。编译期确定大小。
      * 存储单位：Slot（槽），每个 Slot 32 位（4 字节），long double 占用两个连续的slot
      * 索引 0: 实例方法中，索引 0 固定存储 this 指针；静态方法无 this
    * 操作数栈：后进先出（LIFO）的工作区，用于字节码指令执行时的临时数据存储，如算术运算的中间结果。
    * 动态链接：每个栈帧都持有一个指向运行时常量池中该方法所属类的符号引用，用于支持方法的动态分派。
    * 返回地址：方法正常退出或异常退出后，需要恢复上层方法的执行状态，返回地址记录了调用者的 PC 值。
    * 额外信息（如调试信息、锁信息等 (可选)）
* 本地方法栈 native method stack ： 

共享区域：
* 堆 heap：存储对象实例、数组
  * GC算法的主要操作空间，不同算法实现不同，CMS分代 8:1:1 (默认年轻代 Yong Gen中 Eden : from survivor : to survivor)，2:1 (老年代：年轻代 默认，可以用 NewRatio指定)，15(年轻代晋升15次后进入老年代，受 -XX:MaxTenuringThreshold=15 控制，cms默认是6 ！！！！！！！)(-XX:PretenureSizeThreshold 大对象直通:超过此大小的对象，直接在老年代分配，不经过年轻代,默认值：0（无限制，所有对象先走年轻代）,单位Bytes，仅对 Serial Old 和 ParNew 收集器有效（CMS 可用）。G1 收集器无效（G1 用 Humongous 区域处理大对象）)
* 方法区Method Area（JVM规范中定义的逻辑区域） ：java7（包括之前）是永久代PermGen，java8之后是Metaspace
    * 方法区是各个线程共享的内存区域，用于存储已被虚拟机加载的类型信息、字段信息 、方法信息、运行时常量池、即时编译器（JIT）编译后的代码缓存等数据
      * 类型信息：这是方法区最主要的内容。每当 JVM 加载一个类或接口时，它会提取并存储以下信息
        * 全限定名：类的完整包名+类名（例如 java.lang.String）。
        * 修饰符：如 public、abstract、final 等。
        * 直接超类/接口：父类的全限定名以及实现的接口列表。
        * 类型属性：是类还是接口（或是枚举、注解）。
      * 字段信息：类中声明的所有字段（成员变量）的定义信息
        * 字段名称
        * 字段类型（如 int、java.lang.String）
        * 修饰符（public、private、static、final、volatile 等）
      * 方法信息：类中所有方法的定义信息
        * 方法名称
        * 返回类型
        * 参数列表（数量、类型、顺序）
        * 修饰符（public、native、synchronized 等）
        * 方法字节码：即方法内部的实际指令代码
        * 异常表：记录 try-catch 块的范围及捕获的异常类型
      * 运行时常量池：这是方法区中最活跃的部分。每个类被加载后，其 .class 文件中的“常量池表”会被存入这里
        * 字面量（就是代码中显而易见的固定值，在编译期就确定了）：
          * 字符串字面量 String s = "string";
          * 数字字面量 100,3.14
          * 最终常量 public static final int MAX = 100
        * 符号引用（符号引用 是用一组符号来描述所引用的目标（类、字段、方法），它不包含目标在内存中的实际地址）
          * 类和接口（全限定名）：java/lang/String
          * 字段（字段名 + 描述符）：name:Ljava/lang/String
          * 方法（方法名 + 描述符）：println:(Ljava/lang/String;)V
        * 动态解析：在运行期间，JVM 会将这些符号引用转换为直接引用（内存地址）

> Q: 字符串字面量 与 字符串常量池 区别？
> 
> A：字符串字面量是内容 “string abc” , 字符串常量池是字面量存储的位置，当我们定义一个 String str = "abc"时，编译期：编译器发现 "abc" 是字符串字面量，将其记录在 .class 文件的 常量池表 中（符号引用）；类加载期：JVM 加载类，将 .class 文件的常量池表加载到 运行时常量池（方法区/元空间）；运行期（执行到 ldc "abc" 指令时）：JVM 去 字符串常量池（堆中）查找有没有 "abc" 对象，如果有：直接返回该对象的引用，如果没有：在 字符串常量池 中创建一个新的 "abc" 对象，再返回引用，将引用赋值给变量 s 

> Q: 不同版本方法去差异 & 为什么要从永久代转移至元空间？
> 
> A：首先永久代（non-heap）不是堆 heap，但是永久代同样属于jvm定义的内存结构中
> 
> A：为什么要将永久代转变为堆外内存（本地内存）呢，1. 永久代是jvm内存结构定义中的一部分，受jvm内存大小限制，为了缓解永久代oom，使用本地内存不受jvm内存大小限制（受物理内存影响），2.是减少GC压力：类卸载条件苛刻（需 ClassLoader 回收），导致永久代垃圾堆积，触发频繁 Full GC

| 存储内容 | JDK 6 (永久代) | JDK 7 (永久代) | JDK 8+ (元空间) |
| :--- | :--- | :--- | :--- |
| **类型元数据** | 方法区 (永久代) | 方法区 (永久代) | 方法区 (元空间 - 本地内存) |
| **方法字节码** | 方法区 (永久代) | 方法区 (永久代) | 方法区 (元空间 - 本地内存) |
| **运行时常量池** | 方法区 (永久代) | 方法区 (永久代) | 方法区 (元空间 - 本地内存) |
| **静态变量** | 方法区 (永久代) | Java 堆 (Heap) | Java 堆 (Heap) |
| **字符串常量池** | 方法区 (永久代) | Java 堆 (Heap) | Java 堆 (Heap) |

> Q：本地方法栈与 non-heap区域 JNI native memory 区别？
> 
> A：本地方法栈 内部也是存储 (栈帧Stack Frame)同虚拟机栈一样，不过是服务于native方法(操作系统底层方法 ), 而JNI Native Memory 是存储实际数据的位置

> Q: jvm参数示例：
> 
> A：-Xms	堆初始大小	-Xms4G
> 
> -Xmx	堆最大大小	-Xmx4g
> 
> -Xss	每个线程栈大小	-Xss512k   (默认1M)
> 
> -Xmn	新生代大小	-Xmn512m
> 
> -XX:MetaspaceSize	元空间初始大小	-XX:MetaspaceSize=512m
> 
> -XX:MaxMetaspaceSize	元空间最大大小	-XX:MaxMetaspaceSize=512m
> 
> -XX:SurvivorRatio	Eden 与 Survivor 比例	-XX:SurvivorRatio=8
> 
> -XX:NewRatio	old Gen老年代 与 new Gen新生代 比例	-XX:NewRatio=2
> 
> -XX:MaxDirectMemorySize   NIO 直接内存  不指定默认等于 Xmx,主要堆外内存不仅包含Direct Memory(受 MaxDirectMemorySize 限制)，还有元空间 Metaspace(受 MaxMetaspaceSize 限制)，  线程栈 Thread Stacks(受 -Xss 限制)，代码缓存 Code Cache，JNI 手动分配内存 (malloc，完全不受限！)，GC 结构占用等

> 什么是逃逸分析
> 
> 什么是TLAB

## HotSpot对象内存布局 HotSpotObjectMemoryLayout

# java内存模型(JMM) Java Memory Model
> Java语言规范定义的多线程内存访问模型，用于保证共享变量的**可见性**、**原子性**和**有序性**，与垃圾回收无关