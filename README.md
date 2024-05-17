> 源项目：[novel](https://github.com/201206030/novel)

# Novel

## 一、项目概述

基于SpringBoot 3开发的前后端分离的后端项目，目标是构建一个完整的小说门户，由小说门户系统、作家后台管理系统、平台后台管理系统等多个子系统构成。包括小说推荐、作品检索、小说排行榜、小说阅读、小说评论、会员中心、作家专区、充值订阅、新闻发布等功能。

## 二、开发环境

以下为该项目涉及到的主要技术：

| 相关技术              | 版本     | 说明           |
| ----------------- | ------ | ------------ |
| Java              | 17     | -            |
| SpringBoot        | 3.0.7  | -            |
| Redis             | 7.0    | 分布式缓存        |
| Elasticsearch     | 8.2.0  | 搜索引擎         |
| MySQL             | 8.0    | 数据库          |
| MyBatis-Plus      | 3.5.3  | MyBatis的增强工具 |
| RabbitMQ          | 3.10.2 | 消息队列         |
| Sentinel          | 1.8.4  | 流量监控组件       |
| Redisson          | 3.17.4 | 分布式锁         |
| Springdoc-openapi | 2.0.0  | 接口文档         |
| JJWT              | 0.11.5 | JWT 登录支持     |

## 三、项目框架

```
io
 +- github
     +- yeyuhl  
        +- novel
            +- NovelApplication.java -- 项目启动类
            |
            +- core -- 项目核心模块，包括各种工具、配置和常量等
            |   +- common -- 业务无关的通用模块
            |   |   +- exception -- 通用异常处理
            |   |   +- constant -- 通用常量   
            |   |   +- req -- 通用请求数据格式封装，例如分页请求数据  
            |   |   +- resp -- 接口响应工具及响应数据格式封装 
            |   |   +- util -- 通用工具   
            |   | 
            |   +- annotation -- 自定义注解类
            |   +- aspect -- Spring AOP 切面
            |   +- auth -- 用户认证授权相关
            |   +- config -- 业务相关配置
            |   +- constant -- 业务相关常量         
            |   +- filter -- 过滤器 
            |   +- interceptor -- 拦截器
            |   +- json -- JSON 相关的包，包括序列化器和反序列化器
            |   +- task -- 定时任务
            |   +- util -- 业务相关工具 
            |   +- wrapper -- 装饰器
            |
            +- dto -- 数据传输对象，包括对各种 Http 请求和响应数据的封装
            |   +- req -- Http 请求数据封装
            |   +- resp -- Http 响应数据封装
            |
            +- dao -- 数据访问层，与底层 MySQL 进行数据交互
            +- manager -- 通用业务处理层，对第三方平台封装、对 Service 层通用能力的下沉以及对多个 DAO 的组合复用 
            +- service -- 相对具体的业务逻辑服务层  
            +- controller -- 主要是处理各种 Http 请求，各类基本参数校验，或者不复用的业务简单处理，返回 JSON 数据等
            |   +- front -- 小说门户相关接口
            |   +- author -- 作家管理后台相关接口
            |   +- admin -- 平台管理后台相关接口（暂无）
            |   +- app -- app 接口（暂无）
            |   +- applet -- 小程序接口（暂无）
            |   +- open -- 开放接口，供第三方调用（暂无） 
```

## 四、代码实现

### 1. core.common

在common这个包里面，都是一些通用模块，可以优先完成。比如resp类下自定义的通用返回结果，将MyBatis-Plus查询到的Page封装起来，再封装到Http的响应包里面。

### 2. 配置logback-spring.xml

由于SpringBoot使用的是Slf4j作为日志门面（把不同的日志系统的实现进行了具体的抽象化），Logback作为日志实现（真正在干活的日志框架）。由于我们希望输出的log能定制化，方便我们查阅log时能快速定位到问题所在。因此我们可以编写一个logback-spring.xml来进行配置，我们可以参考org.springframework.boot.logging.logback路径下的各种xml配置文件来进行编写。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 隐藏启动时的info -->
    <statusListener class="ch.qos.logback.core.status.NopStatusListener"/>
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter"/>
    <conversionRule conversionWord="wex"
                    converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"/>
    <conversionRule conversionWord="wEx"
                    converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"/>
    <!-- 彩色日志格式 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

    <!-- %m输出的信息,%n表示换行符,%p日志级别,%t线程名,%d日期,%c类的全名,%i索引【从数字0开始递增】,%clr(...){faint}是将括号内的字体设置为淡色,%clr(...){magenta}是颜色设为品红色 -->
    <!-- appender是configuration的子节点，是负责写日志的组件。 -->
    <!-- ConsoleAppender：把日志输出到控制台 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <!--
            <pattern>%d %p (%file:%line\)- %m%n</pattern>
             -->
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <!-- 控制台也要使用UTF-8，不要使用GBK，否则会中文乱码 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- RollingFileAppender：滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件 -->
    <!-- 以下的大概意思是：1.先按日期存日志，日期变了，将前一天的日志文件名重命名为XXX%日期%索引，新的日志仍然是demo.log -->
    <!-- 2.如果日期没有发生变化，但是当前日志的文件大小超过1KB时，对当前日志进行分割 重命名 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">

        <File>logs/novel.log</File>
        <!-- rollingPolicy:当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名。 -->
        <!-- TimeBasedRollingPolicy： 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 文件保存位置以及文件命名规则，这里用到了%d{yyyy-MM-dd}表示当前日期，%i表示这一天的第N个日志 -->
            <!-- 活动文件的名字会根据fileNamePattern的值，每隔一段时间改变一次 -->
            <!-- 文件名：logs/demo.2017-12-05.0.log -->
            <fileNamePattern>logs/debug.%d.%i.log</fileNamePattern>
            <!-- 到期自动清理日志文件 -->
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
            <!-- 每产生一个日志文件，该日志文件的保存期限为30天 -->
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <!-- maxFileSize:这是活动文件的大小，默认值是10MB，测试时可改成1KB看效果 -->
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <encoder>
            <!-- pattern节点，用来设置日志的输入格式 -->
            <pattern>
                %d %p (%file:%line\)- %m%n
            </pattern>
            <!-- 记录日志的编码:此处设置字符集 - -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <springProfile name="dev">
        <!-- ROOT 日志级别 -->
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
        </root>
        <!-- 指定项目中某个包，当有日志操作行为时的日志记录级别 -->
        <!-- io.github.yeyuhl 为根包，也就是只要是发生在这个根包下面的所有日志操作行为的权限都是DEBUG -->
        <!-- 级别依次为【从高到低】：FATAL > ERROR > WARN > INFO > DEBUG > TRACE -->
        <logger name="io.github.yeyuhl" level="DEBUG" additivity="false">
            <appender-ref ref="STDOUT"/>
        </logger>
    </springProfile>

    <springProfile name="prod">
        <!-- ROOT 日志级别 -->
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="FILE"/>
        </root>
        <!-- 指定项目中某个包，当有日志操作行为时的日志记录级别 -->
        <!-- io.github.yeyuhl 为根包，也就是只要是发生在这个根包下面的所有日志操作行为的权限都是DEBUG -->
        <!-- 级别依次为【从高到低】：FATAL > ERROR > WARN > INFO > DEBUG > TRACE -->
        <logger name="io.github.yeyuhl" level="ERROR" additivity="false">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="FILE"/>
        </logger>
    </springProfile>
</configuration>
```

### 3. 缓存相关

#### 1）缓存变量

在core.constant包内新建CacheConsts类，包含各种小说缓存，比如小说分类列表缓存，小说内容缓存之类的，还包含一些缓存配置常量。

#### 2）缓存配置

在core.config包内新建CacheConfig类，在里面实现caffeineCacheManager和redisCacheManager两个方法，自定义缓存管理器。caffeineCacheManager管理的是本地相关的缓存，而redisCacheManager则管理远程相关的缓存。然后在manager的cache包下设置各个业务模块的CacheManager，当请求到来时候优先访问缓存以此提高性能。

我们使用Spring的@Cacheable注解来实现缓存管理，@Cacheable是作用在方法上的，其核心思想是这样的：当我们在调用一个缓存方法时会把该方法参数和返回结果作为一个键值对存放在缓存中，等到下次利用同样的参数来调用该方法时将不再执行该方法，而是直接从缓存中获取结果进行返回。而@CacheEvict标记的方法会在方法执行前或者执行后移除Spring Cache中的某些元素。

最后来讲一下这个缓存设计。本地缓存虽然有着访问速度快的优点，但无法进行大数据的存储。所以本地缓存一般适合于缓存只读数据，如统计类数据，或者每个部署节点独立的数据。其它情况就需要用到分布式缓存了。注意这并不是一个二级缓存设计，即先查找Caffeine再查找Redis，因此不需要考虑太多两个缓存之间如何同步的问题。更新相关缓存也是采用了最经典的旁路缓存模式，即先更新数据库，然后直接删除对应缓存。当用户查询时，再从数据库读取数据到缓存中。

### 4. 数据库设计

#### 1）MySQL数据库设计规范

- 表达是与否概念的字段，必须使用 is_xxx 的方式命名，数据类型是 unsigned tinyint（ 1 表示是， 0 表示否）。

> 任何字段如果为非负数，必须是 unsigned ，坚持 is_xxx 的命名方式是为了明确其取值含义与取值范围。表达逻辑删除的字段名 is_deleted，1表示删除，0表示未删除。

- 表名、字段名必须使用小写字母或数字， 禁止出现数字开头，禁止两个下划线中间只出现数字。数据库字段名的修改代价很大，字段名称需要慎重考虑。

> MySQL 在 Windows 下不区分大小写，但在 Linux 下默认是区分大小写。因此，数据库名、表名、字段名，都不允许出现任何大写字母，避免节外生枝。

- 表名不使用复数名词。

> 表名应该仅仅表示表里面的实体内容，不应该表示实体数量，对应于 DO 类名也是单数形式，符合表达习惯。

- 禁用保留字，如 desc、 range、 match、 delayed 等， 请参考 MySQL 官方保留字。

- 主键索引名为 pk_字段名；唯一索引名为 uk_字段名；普通索引名则为 idx_字段名。

> pk_ 即 primary key； uk_ 即 unique key； idx_ 即 index 的简称。

- 小数类型为 decimal，禁止使用 float 和 double。

> 在存储的时候， float 和 double 都存在精度损失的问题，很可能在比较值的时候，得到不正确的结果。如果存储的数据范围超过 decimal 的范围，建议将数据拆成整数和小数并分开存储。

- 如果存储的字符串长度几乎相等，使用 char 定长字符串类型。

- varchar 是可变长字符串，不预先分配存储空间，长度不要超过 5000，如果存储长度大于此值，定义字段类型为 text，独立出来一张表，用主键来对应，避免影响其它字段索引效率。

- 表必备三字段： id, create_time, update_time。

> 其中 id 必为主键，类型为 bigint unsigned、单表时自增、步长为 1。 create_time, update_time的类型均为 datetime 类型，前者现在时表示主动式创建，后者过去分词表示被动式更新。值得注意的是，在更新数据表记录时，必须同时更新记录对应的 update_time 字段值为当前时间。

- 表的命名最好是遵循“业务名称_表的作用” 。

> 如book_info / book_chapter / user_bookshelf / user_comment / author_info

- 库名与应用名称尽量一致。

- 如果修改字段含义或对字段表示的状态追加时，需要及时更新字段注释。

- 字段允许适当冗余，以提高查询性能，但必须考虑数据一致。冗余字段应遵循：
  
  - 不是频繁修改的字段。
  
  - 不是唯一索引的字段。
  
  - 不是 varchar 超长字段，更不能是 text 字段。

> 各业务线经常冗余存储小说名称，避免查询时需要连表（单体应用）或跨服务（微服务应用）获取。

- 单表行数超过 500 万行或者单表容量超过 2GB，才推荐进行分库分表。

> 如果预计三年后的数据量根本达不到这个级别，请不要在创建表时就分库分表

- 合适的字符存储长度，不但节约数据库表空间、节约索引存储，更重要的是提升检索速度。

> 无符号值可以避免误存负数， 且扩大了表示范围。

- 业务上具有唯一特性的字段，即使是组合字段，也必须建成唯一索引。

> 不要以为唯一索引影响了 insert 速度，这个速度损耗可以忽略，但提高查找速度是明显的； 另外，即使在应用层做了非常完善的校验控制，只要没有唯一索引，根据墨菲定律，必然有脏数据产生。

- 超过三个表禁止 join。需要 join 的字段，数据类型保持绝对一致； 多表关联查询时，保证被关联的字段需要有索引。

> 即使双表 join 也要注意表索引、 SQL 性能。

- 在 varchar 字段上建立索引时，必须指定索引长度，没必要对全字段建立索引，根据实际文本区分度决定索引长度。

> 索引的长度与区分度是一对矛盾体，一般对字符串类型数据，长度为 20 的索引，区分度会高达 90%以上，可以使用 count(distinct left(列名, 索引长度))/count(*)的区分度来确定。

- 创建索引时避免有如下极端误解：
  
  - 索引宁滥勿缺。 认为一个查询就需要建一个索引。
  
  - 吝啬索引的创建。 认为索引会消耗空间、 严重拖慢记录的更新以及行的新增速度。
  
  - 抵制唯一索引。 认为唯一索引一律需要在应用层通过“先查后插”方式解决。

#### 2）数据库建模

可以使用国产PDManer来建模，比PowerDesigner更加好用。注意，虽然建模的时候标出了外键，但是实际编写SQL语句的时候，尽量不使用外键，因为随着需求不断变化，外键成为了累赘。

**首页模块**

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918151325.png)

**新闻模块**

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918151338.png)

**小说模块**

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918151345.png)

**用户模块**

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918151352.png)

**作家模块**

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918151400.png)

**支付模块**

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918151408.png)

**系统模块**

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918151415.png)

### 5. 生成代码

#### 1）设置Mybatis-Plus

在NovelApplication中配置 MapperScan 注解，然后在core.config中添加MybatisPlusConfig配置类，将里面的分页插件相关的方法设置为Bean。

#### 2）使用Mybatis-Plus-Generator

由于生成代码是一次性操作，所以可以放到test目录下。在test的resource目录里创建需要的模板文件，然后在java目录下创建Generator类来实现代码的生成。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230506175050.png)

生成代码的配置基本都差不多，全局配置，数据源配置（可以和全局配置一起），包配置，模板配置，策略配置。其中模板部分可以不用自己编写模板，可以直接`new TemplateConfig.Builder().build()`使用预设模板。但要注意，哪怕自己不写模板，也需要配置Velocity模板引擎或者其他模板引擎。

### 6. 完成DTO

DTO是Data Transfer Object的缩写，意为**数据传输对象**。服务层要向展示层即前端传递数据，为了安全性和规范性考虑，需要对数据进行封装。注意DTO中AuthorInfoDto和UserInfoDto还有一些其他类需要序列化，此处因为是简单的序列化因此直接使用Java默认的序列化。然后因为安全性问题，比如BookCommentRespDto中一些数据的序列化需要调用自己编写的UsernameSerializer工具类(在core.json)，进行脱敏处理。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230507203800.png)

### 7. 创建service并实现

根据业务需求编写service接口，一共有作者，小说，首页，新闻，资源，搜索，用户七大模块。其中小说模块的相关业务是最为繁重，而资源，搜索，用户需要特定中间件，因此优先实现其他模块的服务类，后面才处理剩余模块。

#### 1）AuthorServiceImpl

> 实现AuthorServiceImpl需要先实现以下类：
> 
> manager.cache.AuthorInfoCacheManager 管理作家信息相关的缓存
> 
> core.constant.DatabaseConsts 数据库常量类
> 
> core.constant.SystemConfigConsts 系统配置常量类

AuthorService有两个方法，一个是作家注册的方法，一个是查询作家状态的方法。前者先检验作家是否注册，如果已经注册则直接返回，如果没注册则进行注册并清空缓存再返回。后者则是如果作家存在，则返回0，如果作家不存在，返回null。

#### 2）BookServiceImpl

> 实现BookServiceImpl需要先实现以下类：
> 
> core.annotation.Key 分布式锁-Key 注解
> 
> core.annotation.Lock 分布式锁 注解
> 
> core.aspect.LockAspect 分布式锁切面
> 
> core.auth.UserHolder
> 
> manager.cache.BookCategoryCacheManager 小说分类缓存管理类
> 
> manager.cache.BookRankCacheManager 小说排行榜缓存管理类
> 
> manager.cache.BookInfoCacheManager 小说信息缓存管理类
> 
> manager.cache.BookChapterCacheManager 小说章节缓存管理类
> 
> manager.cache.BookContentCacheManager 小说内容缓存管理类
> 
> manager.dao.UserDaoManager 用户模块DAO管理类
> 
> manager.mq.AmqpMsgManager AMQP消息管理类

BookService涉及到的业务最多，因此需要实现的方法也最多，部分方法需要用到上述需要实现的类，还有部分方法需要自己修改mapper.xml来实现查询。

```java
    /**
     * 小说点击榜查询
     *
     * @return 小说点击排行列表
     */
    RestResp<List<BookRankRespDto>> listVisitRankBooks();

    /**
     * 小说新书榜查询
     *
     * @return 小说新书排行列表
     */
    RestResp<List<BookRankRespDto>> listNewestRankBooks();

    /**
     * 小说更新榜查询
     *
     * @return 小说更新排行列表
     */
    RestResp<List<BookRankRespDto>> listUpdateRankBooks();

    /**
     * 小说信息查询
     *
     * @param bookId 小说ID
     * @return 小说信息
     */
    RestResp<BookInfoRespDto> getBookById(Long bookId);

    /**
     * 小说内容相关信息查询
     *
     * @param chapterId 章节ID
     * @return 内容相关联的信息
     */
    RestResp<BookContentAboutRespDto> getBookContentAbout(Long chapterId);

    /**
     * 小说最新章节相关信息查询
     *
     * @param bookId 小说ID
     * @return 章节相关联的信息
     */
    RestResp<BookChapterAboutRespDto> getLastChapterAbout(Long bookId);

    /**
     * 小说推荐列表查询
     *
     * @param bookId 小说ID
     * @return 小说信息列表
     */
    RestResp<List<BookInfoRespDto>> listRecBooks(Long bookId) throws NoSuchAlgorithmException;

    /**
     * 增加小说点击量
     *
     * @param bookId 小说ID
     * @return 成功状态
     */
    RestResp<Void> addVisitCount(Long bookId);

    /**
     * 获取上一章节ID
     *
     * @param chapterId 章节ID
     * @return 上一章节ID
     */
    RestResp<Long> getPreChapterId(Long chapterId);

    /**
     * 获取下一章节ID
     *
     * @param chapterId 章节ID
     * @return 下一章节ID
     */
    RestResp<Long> getNextChapterId(Long chapterId);

    /**
     * 小说章节列表查询
     *
     * @param bookId 小说ID
     * @return 小说章节列表
     */
    RestResp<List<BookChapterRespDto>> listChapters(Long bookId);

    /**
     * 小说分类列表查询
     *
     * @param workDirection 作品方向;0-男频 1-女频
     * @return 分类列表
     */
    RestResp<List<BookCategoryRespDto>> listCategory(Integer workDirection);

    /**
     * 发表评论
     *
     * @param dto 评论相关 DTO
     * @return void
     */
    RestResp<Void> saveComment(UserCommentReqDto dto);

    /**
     * 小说最新评论查询
     *
     * @param bookId 小说ID
     * @return 小说最新评论数据
     */
    RestResp<BookCommentRespDto> listNewestComments(Long bookId);

    /**
     * 删除评论
     *
     * @param userId    评论用户ID
     * @param commentId 评论ID
     * @return void
     */
    RestResp<Void> deleteComment(Long userId, Long commentId);

    /**
     * 修改评论
     *
     * @param userId  用户ID
     * @param id      评论ID
     * @param content 修改后的评论内容
     * @return void
     */
    RestResp<Void> updateComment(Long userId, Long id, String content);

    /**
     * 小说信息保存
     *
     * @param dto 小说信息
     * @return void
     */
    RestResp<Void> saveBook(BookAddReqDto dto);

    /**
     * 小说章节信息保存
     *
     * @param dto 章节信息
     * @return void
     */
    RestResp<Void> saveBookChapter(ChapterAddReqDto dto);

    /**
     * 查询作家发布小说列表
     *
     * @param dto 分页请求参数
     * @return 小说分页列表数据
     */
    RestResp<PageRespDto<BookInfoRespDto>> listAuthorBooks(PageReqDto dto);

    /**
     * 查询小说发布章节列表
     *
     * @param bookId 小说ID
     * @param dto    分页请求参数
     * @return 章节分页列表数据
     */
    RestResp<PageRespDto<BookChapterRespDto>> listBookChapters(Long bookId, PageReqDto dto);

    /**
     * 分页查询评论
     *
     * @param userId     会员ID
     * @param pageReqDto 分页参数
     * @return 评论分页列表数据
     */
    RestResp<PageRespDto<UserCommentRespDto>> listComments(Long userId, PageReqDto pageReqDto);

    /**
     * 小说章节删除
     *
     * @param chapterId 章节ID
     * @return void
     */
    RestResp<Void> deleteBookChapter(Long chapterId);

    /**
     * 小说章节查询
     *
     * @param chapterId 章节ID
     * @return 章节内容
     */
    RestResp<ChapterContentRespDto> getBookChapter(Long chapterId);

    /**
     * 小说章节更新
     *
     * @param chapterId 章节ID
     * @param dto       更新内容
     * @return void
     */
    RestResp<Void> updateBookChapter(Long chapterId, ChapterUpdateReqDto dto);
```

#### 3）HomeServiceImpl

> 实现HomeServiceImpl需要先实现以下类：
> 
> manager.cache.HomeBookCacheManager 首页推荐小说缓存管理类
> 
> manager.cache.FriendLinkCacheManager 友情链接缓存管理类

HomeServiceI有两个方法，一个是listHomeBooks，查询首页小说推荐列表，一个是listHomeFriendLinks，查询首页友情链接列表。

#### 4）NewsServiceImpl

> 实现NewsServiceImpl需要先实现以下类：
> 
> manager.cache.NewsCacheManager 新闻缓存管理类

NewsServiceI有两个方法，一个是listLatestNews，查询最新新闻列表，一个是getNews，查询新闻信息。

#### 5）ResourceServiceImpl

> 实现ResourceServiceImpl需要先实现以下类：
> 
> core.common.utils.ImgVerifyCodeUtils 图形验证码工具类
> 
> manager.redis.VerifyCodeManager 验证码管理类

ResourceServiceImpl有两个方法，一个是getImgVerifyCode，获取图片验证码，一个是uploadImage，用于上传图片。

#### 6）UserServiceImpl

> 实现UserServiceImpl需要先实现以下类：
> 
> core.utils.JwtUtils JWT的工具类

```java
    /**
     * 用户注册
     *
     * @param dto 注册参数
     * @return JWT
     */
    RestResp<UserRegisterRespDto> register(UserRegisterReqDto dto);

    /**
     * 用户登录
     *
     * @param dto 登录参数
     * @return JWT + 昵称
     */
    RestResp<UserLoginRespDto> login(UserLoginReqDto dto);

    /**
     * 用户反馈
     *
     * @param userId  反馈用户ID
     * @param content 反馈内容
     * @return void
     */
    RestResp<Void> saveFeedback(Long userId, String content);

    /**
     * 用户信息修改
     *
     * @param dto 用户信息
     * @return void
     */
    RestResp<Void> updateUserInfo(UserInfoUpdateReqDto dto);

    /**
     * 用户反馈删除
     *
     * @param userId 用户ID
     * @param id     反馈ID
     * @return void
     */
    RestResp<Void> deleteFeedback(Long userId, Long id);

    /**
     * 查询书架状态接口
     *
     * @param userId 用户ID
     * @param bookId 小说ID
     * @return 0-不在书架 1-已在书架
     */
    RestResp<Integer> getBookshelfStatus(Long userId, String bookId);

    /**
     * 用户信息查询
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    RestResp<UserInfoRespDto> getUserInfo(Long userId);
```

#### 7）DbSearchServiceImpl和EsSearchServiceImpl

> 实现DbSearchServiceImpl需要先实现以下类：
> 
> dao.mapper.BookInfoMapper 小说信息Mapper接口

BookInfoMapper需要声明searchBooks这个方法，然后去xml里面实现，xml中的`<if test="..."> </if>`是MyBatis的动态SQL语句，只有if判断的语句成立，才会添加相应的SQL语句到前面SQL语句的后面。

```xml
 <select id="searchBooks" resultType="io.github.yeyuhl.novel.dao.entity.BookInfo">
        select id,
        category_id,
        category_name,
        book_name,
        author_id,
        author_name,
        word_count,
        last_chapter_name
        from book_info
        where word_count > 0
        <if test="condition.keyword != null and condition.keyword != ''">
            and (book_name like concat('%',#{condition.keyword},'%') or author_name like concat('%',#{condition.keyword},'%'))
        </if>
        <if test="condition.workDirection != null">
            and work_direction = #{conditionDirection}
        </if>
        <if test="condition.categoryId != null">
            and category_id = #{condition.categoryId}
        </if>
        <if test="condition.isVip != null">
            and is_vip = #{condition.isVip}
        </if>
        <if test="condition.bookStatus != null">
            and book_status = #{condition.bookStatus}
        </if>
        <if test="condition.wordCountMin != null">
            and word_count >= #{condition.wordCountMin}
        </if>
        <if test="condition.wordCountMax != null">
            and word_count <![CDATA[ < ]]> #{condition.wordCountMax}
        </if>
        <if test="condition.updateTimeMin != null">
            and last_chapter_update_time >= #{condition.updateTimeMin}
        </if>
        <if test="condition.sort != null">
            order by ${condition.sort}
        </if>
    </select>
```

这个类里面只需要实现一个search方法，在DbSearchServiceImpl里实现的search方法则是在数据库里面搜索。

> 实现EsSearchServiceImpl需要先实现以下类：
> 
> core.constant.EsConsts elasticsearch相关常量

这个类里面只需要实现一个search方法，在EsSearchServiceImpl里实现的search方法则是调用elasticsearch的方法来搜索。

### 8. controller

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230517140747.png)

在Controller中，我们会用到各种服务，我们可以使用@Autowired来进行依赖注入，也可以使用其他方式。

- **@Autowired**
  
  ```java
  @RestController
  @RequestMapping(ApiRouterConsts.API_AUTHOR_URL_PREFIX)
  public class AuthorController {
      @Autowired
      AuthorService authorService;
  
      /**
       * 查询作家状态接口
       */
      @Operation(summary = "作家状态查询接口")
      @GetMapping("status")
      public RestResp<Integer> getStatus() {
          return authorService.getStatus(UserHolder.getUserId());
      }
  }
  ```

- **final + 构造函数**
  
  ```java
  @RestController
  @RequestMapping(ApiRouterConsts.API_AUTHOR_URL_PREFIX)
  public class AuthorController {
  
      private final AuthorService authorService;
  
      public AuthorController(AuthorService authorService) {
          this.authorService = authorService;
      }
  
      /**
       * 查询作家状态接口
       */
      @Operation(summary = "作家状态查询接口")
      @GetMapping("status")
      public RestResp<Integer> getStatus() {
          return authorService.getStatus(UserHolder.getUserId());
      }
  }
  ```

- **final + @RequiredArgsConstructor**
  
  ```java
  @RestController
  @RequestMapping(ApiRouterConsts.API_AUTHOR_URL_PREFIX)
  @RequiredArgsConstructor
  public class AuthorController {
  
      private final AuthorService authorService;
  
      /**
       * 查询作家状态接口
       */
      @Operation(summary = "作家状态查询接口")
      @GetMapping("status")
      public RestResp<Integer> getStatus() {
          return authorService.getStatus(UserHolder.getUserId());
      }
  }
  ```

本质上第二种和第三种是一样的，Spring Boot官方建议使用final来修饰成员变量，这样成员变量无法被修改，再通过构造方法来进行注入，这样能：

1. 防止已经注入的成员变量被修改。

2. 避免以后因修改代码，例如将方法的返回值更新为null，而造成恶劣影响。

## 五、项目优化

### ElasticSearch

首先看看官方自己的介绍：

> Elasticsearch 是一个分布式、RESTful 风格的搜索和数据分析引擎，能够解决不断涌现出的各种用例。作为 Elastic Stack 的核心，Elasticsearch 会集中存储您的数据，让您飞快完成搜索，微调相关性，进行强大的分析，并轻松缩放规模。

不难看出ElasticSearch就是一个功能强大的搜索引擎，与其相关的 Logstach 和 Beats 促进采集、合计以及充实你的数据并在 Elasticsearch 中存储它们。Kibana 允许你去交互式的探索、可视化和共享对数据的见解，以及监视这个栈（Elastic Stack）。ElasticSearch就是这个技术栈的核心。

#### 1. 简单介绍

为了快速理解Elasticsearch的基础概念，我们将其与MySQL进行类比：

| Elasticsearch | MySQL    |
| ------------- | -------- |
| Index         | Database |
| Type          | Table    |
| Documents     | Row      |
| Fields        | Column   |

上面所提到的Type这个概念已经被弱化了，在Elasticsearch6.x中，一个Index下只能包含一个Type，而在Elasticsearch7.x中，Type这个概念已经被删除。

与MySQL的正排索引机制不同，Elasticsearch用的是倒排索引。正排索引是用文章编号去找文章，如果我要搜索文章里面的内容，做模糊搜索，就需要一篇一篇文章找。而倒排索引则是提取文章中的关键词，并记录下关键词出现在的文章的编号。

**正排索引**：

| id   | content              |
| ---- | -------------------- |
| 1001 | my name is zhang san |
| 1002 | my name is li si     |

**倒排索引**：

| keyword | id         |
| ------- | ---------- |
| name    | 1001, 1002 |
| zhang   | 1001       |

#### 2. 快速上手

在Kibana中创建索引：

```java
// properties是一个字段的子字段，即一个字段可以有多个子字段
// type是字段数据类型，常用的有text，keyword，数值(long,short)，布尔，日期
// analyzer是分词器，将字符串分解成想要的单元
// doc_values是指是否存储字段的原始值，默认为true
// index则是指是否创建索引（分词），默认也为true
PUT /book
{
  "mappings" : {
    "properties" : {
      "id" : {
        "type" : "long"
      },
      "authorId" : {
        "type" : "long"
      },
      "authorName" : {
        "type" : "text",
        "analyzer": "ik_smart"
      },
      "bookName" : {
        "type" : "text",
        "analyzer": "ik_smart"
      },
      "bookDesc" : {
        "type" : "text",
        "analyzer": "ik_smart"
      },
      "bookStatus" : {
        "type" : "short"
      },
      "categoryId" : {
        "type" : "integer"
      },
      "categoryName" : {
        "type" : "text",
        "analyzer": "ik_smart"
      },
      "lastChapterId" : {
        "type" : "long"
      },
      "lastChapterName" : {
        "type" : "text",
        "analyzer": "ik_smart"
      },
      "lastChapterUpdateTime" : {
        "type": "long"
      },
      "picUrl" : {
        "type" : "keyword",
        "index" : false,
        "doc_values" : false
      },
      "score" : {
        "type" : "integer"
      },
      "wordCount" : {
        "type" : "integer"
      },
      "workDirection" : {
        "type" : "short"
      },
      "visitCount" : {
        "type": "long"
      }
    }
  }
}
```

相关Java代码：

```java
// Elasticsearch客户端
private final ElasticsearchClient esClient;
public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition) {

        SearchResponse<EsBookDto> response = esClient.search(s -> {

                    SearchRequest.Builder searchBuilder = s.index(EsConsts.BookIndex.INDEX_NAME);
                    // 构建检索条件
                    buildSearchCondition(condition, searchBuilder);
                    // 排序
                    if (!StringUtils.isBlank(condition.getSort())) {
                        searchBuilder.sort(o -> o.field(f -> f
                                // 将下划线转换为驼峰，并且从condition中获取排序字段并且按空格分隔
                                .field(StringUtils.underlineToCamel(condition.getSort().split(" ")[0]))
                                // 降序
                                .order(SortOrder.Desc)));
                    }
                    // 分页，from(x)指定从第x页开始查询，size(y)指定每页显示y条数据
                    searchBuilder.from((condition.getPageNumber() - 1) * condition.getPageSize())
                            .size(condition.getPageSize());
                    // 设置高亮显示
                    searchBuilder.highlight(h -> h.fields(EsConsts.BookIndex.FIELD_BOOK_NAME,
                                    t -> t.preTags("<em style='color:red'>").postTags("</em>"))
                            .fields(EsConsts.BookIndex.FIELD_AUTHOR_NAME,
                                    t -> t.preTags("<em style='color:red'>").postTags("</em>")));

                    return searchBuilder;
                },
                EsBookDto.class
        );

        // 获取总命中数
        TotalHits total = response.hits().total();

        List<BookInfoRespDto> list = new ArrayList<>();
        List<Hit<EsBookDto>> hits = response.hits().hits();
        // 遍历hits，将hits中的数据放入list中
        for (var hit : hits) {
            // 获取source
            EsBookDto book = hit.source();
            assert book != null;
            // 如果高亮显示的字段不为空，那么将高亮显示的字段放入book中
            if (!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME))) {
                book.setBookName(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME).get(0));
            }
            if (!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME))) {
                book.setAuthorName(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME).get(0));
            }
            // 将book放入list中
            list.add(BookInfoRespDto.builder()
                    .id(book.getId())
                    .bookName(book.getBookName())
                    .categoryId(book.getCategoryId())
                    .categoryName(book.getCategoryName())
                    .authorId(book.getAuthorId())
                    .authorName(book.getAuthorName())
                    .wordCount(book.getWordCount())
                    .lastChapterName(book.getLastChapterName())
                    .build());
        }
        assert total != null;
        return RestResp.ok(PageRespDto.of(condition.getPageNumber(), condition.getPageSize(), total.value(), list));

    }
```

构建检索条件的代码分析如下：

```java
            // 首先条件是，只查询有字数的小说
            b.must(RangeQuery.of(m -> m
                    // 查询字段是FIELD_WORD_COUNT
                    .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                    // 要求小说总字数是大于0的
                    .gt(JsonData.of(0))
            )._toQuery());
```

上面的b是指BoolQuery，在Elasticsearch中，BoolQuery是一种用于组合多个查询子句的查询。它允许通过must、mustNot和should等子句来指定多个查询条件，并通过它们之间的逻辑关系组合成完整的查询。BoolQuery的作用是：

- 组合多个查询子句，通过逻辑运算构建更复杂的查询。
- 使用must、mustNot和should等子句来指定不同查询条件之间的逻辑关系。
- 可以在一个查询里组合文本匹配查询、过滤、类似度评分等多种技术。

BoolQuery的子句如下：

- must：文档必须满足所有的must子句，才能匹配查询。
- mustNot：文档不能满足所有的mustNot子句，才能匹配查询。
- should：文档只要满足任意一个或多个should子句，才能匹配查询。
- filter：文档满足filter子句，但不影响查询的评分。

BoolQuery的子句之间默认的逻辑关系是AND，也就是说，文档必须满足所有的must子句，才能匹配查询。mustNot子句和should子句的逻辑关系也可以通过minimumShouldMatch方法来指定。

```java
            // 如果传进来查询条件里的关键词不为空
            if (!StringUtils.isBlank(condition.getKeyword())) {
                // 关键词匹配，可以对多个字段进行匹配，并且可以设置权重
                b.must((q -> q.multiMatch(t -> t
                        .fields(EsConsts.BookIndex.FIELD_BOOK_NAME + "^2",
                                EsConsts.BookIndex.FIELD_AUTHOR_NAME + "^1.8",
                                EsConsts.BookIndex.FIELD_BOOK_DESC + "^0.1")
                        .query(condition.getKeyword()))
                ));
            }
```

multiMatch查询允许在多个字段中进行匹配，并可以指定每个字段的权重。在代码中，我们指定了三个字段：BOOK_NAME、AUTHOR_NAME和BOOK_DESC。其中BOOK_NAME的权重为2，AUTHOR_NAME的权重为1.8，BOOK_DESC的权重为0.1。这意味着，BOOK_NAME字段在匹配时会被给予更高的权重。query()方法接受一个字符串参数，该参数是要匹配的关键词。在代码中，我们使用condition.getKeyword()方法来获取用户输入的关键词。最终，这段代码会返回所有包含指定关键词的文档。

构建检索条件的完整代码如下：

```java
private void buildSearchCondition(BookSearchReqDto condition, SearchRequest.Builder searchBuilder) {

        BoolQuery boolQuery = BoolQuery.of(b -> {

            // 首先条件是，只查询有字数的小说
            b.must(RangeQuery.of(m -> m
                    // 查询字段是FIELD_WORD_COUNT
                    .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                    // 要求小说总字数是大于0的
                    .gt(JsonData.of(0))
            )._toQuery());

            // 如果传进来查询条件里的关键词不为空
            if (!StringUtils.isBlank(condition.getKeyword())) {
                // 关键词匹配，可以对多个字段进行匹配，并且可以设置权重
                b.must((q -> q.multiMatch(t -> t
                        .fields(EsConsts.BookIndex.FIELD_BOOK_NAME + "^2",
                                EsConsts.BookIndex.FIELD_AUTHOR_NAME + "^1.8",
                                EsConsts.BookIndex.FIELD_BOOK_DESC + "^0.1")
                        .query(condition.getKeyword()))
                ));
            }

            // 精确查询
            if (Objects.nonNull(condition.getWorkDirection())) {
                b.must(TermQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_WORK_DIRECTION)
                        .value(condition.getWorkDirection())
                )._toQuery());
            }
            if (Objects.nonNull(condition.getCategoryId())) {
                b.must(TermQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_CATEGORY_ID)
                        .value(condition.getCategoryId())
                )._toQuery());
            }

            // 范围查询
            if (Objects.nonNull(condition.getWordCountMin())) {
                b.must(RangeQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                        // 查找小说总字数大于等于wordCountMin的小说
                        .gte(JsonData.of(condition.getWordCountMin()))
                )._toQuery());
            }
            if (Objects.nonNull(condition.getWordCountMax())) {
                b.must(RangeQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                        // 查找小说总字数小于等于wordCountMax的小说
                        .lt(JsonData.of(condition.getWordCountMax()))
                )._toQuery());
            }
            if (Objects.nonNull(condition.getUpdateTimeMin())) {
                b.must(RangeQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_LAST_CHAPTER_UPDATE_TIME)
                        // 查找小说最后更新时间大于等于updateTimeMin的小说
                        .gte(JsonData.of(condition.getUpdateTimeMin().getTime()))
                )._toQuery());
            }

            return b;

        });
        // 将构建好的boolQuery放入searchBuilder中
        searchBuilder.query(q -> q.bool(boolQuery));
    }
```

#### 3. 文档分析

分析包含下面的过程：

- 将一块文本分成适合于倒排索引的独立的词条。

- 将这些词条统一化为标准格式以提高它们的“可搜索性”，或者recall。

分析器执行上面的工作。分析器实际上是将三个功能封装到了一个包里：

- 字符过滤器：首先，字符串按顺序通过每个 字符过滤器 。他们的任务是在分词前整理字符串。一个字符过滤器可以用来去掉 HTML，或者将 & 转化成 and。

- 分词器：其次，字符串被分词器分为单个的词条。一个简单的分词器遇到空格和标点的时候，可能会将文本拆分成词条。

- Token 过滤器：最后，词条按顺序通过每个 token 过滤器 。这个过程可能会改变词条（例如，小写化Quick ），删除词条（例如， 像 a， and， the 等无用词），或者增加词条（例如，像jump和leap这种同义词）

Elasticsearch还附带了可以直接使用的预包装的分析器，一个分析器必须有一个唯一的分词器。分词器把字符串分解成单个词条或者词汇单元。标准分析器里使用的标准分词器把一个字符串根据单词边界分解成单个词条，并且移除掉大部分的标点符号，然而还有其他不同行为的分词器存在。不过其默认分词器无法识别中文中测试、单词这样的词汇，而是简单的将每个字拆完分为一个词。因此我们需要下载一个IK中文分词器来进行替换。

想要了解更多可以参考：

[ElasticSearch8.X的JavaApi使用](https://juejin.cn/post/7147861385970450440#heading-15)

[Elasticsearch学习笔记](https://blog.csdn.net/u011863024/article/details/115721328)

### XXL-JOB

#### 1. 简单介绍

XXL-JOB是一个分布式任务调度平台，其核心设计目标是开发迅速、学习简单、轻量级、易扩展。XXL-JOB主要由两部分组成：

- **调度模块（调度中心）** 
  负责管理调度信息，按照调度配置发出调度请求，自身不承担业务代码。调度系统与任务解耦，提高了系统可用性和稳定性，同时调度系统性能不再受限于任务模块； 
  支持可视化、简单且动态的管理调度信息，包括任务新建，更新，删除，GLUE开发和任务报警等，所有上述操作都会实时生效，同时支持监控调度结果以及执行日志，支持执行器Failover。

- **执行模块（执行器）** 负责接收调度请求并执行任务逻辑。任务模块专注于任务的执行等操作，开发和维护更加简单和高效；
  
  接收“调度中心”的执行请求、终止请求和日志请求等。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918140135.png)

#### 2. 快速上手

**初始化调度数据库**

请下载项目源码并解压，获取 “调度数据库初始化SQL脚本” 并执行即可。

“调度数据库初始化SQL脚本” 位置为：

`/xxl-job/doc/db/tables_xxl_job.sql`

调度中心支持集群部署，集群情况下各节点务必连接同一个mysql实例；如果mysql做主从，调度中心集群节点务必强制走主库。

**编译源码**

解压源码,按照maven格式将源码导入IDE，使用maven进行编译即可，源码结构如下：

```shell
xxl-job-admin：调度中心
xxl-job-core：公共依赖
xxl-job-executor-samples：执行器Sample示例（选择合适的版本执行器，可直接使用，也可以参考其并将现有项目改造成执行器）
    :xxl-job-executor-sample-springboot：Springboot版本，通过Springboot管理执行器，推荐这种方式；
    :xxl-job-executor-sample-frameless：无框架版本；
```

**部署调度中心**

这里以Docker镜像的方式来搭建：

```shell
/**
* 如需自定义 mysql 等配置，可通过 "-e PARAMS" 指定，参数格式 PARAMS="--key=value  --key2=value2" ；
* 如需自定义 JVM 内存参数 等配置，可通过 "-e JAVA_OPTS" 指定，参数格式 JAVA_OPTS="-Xmx512m" ；
*/
docker pull xuxueli/xxl-job-admin
docker run \
 -e PARAMS=' \
 --spring.datasource.url=jdbc:mysql://47.106.243.172:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai \
 --spring.datasource.username=test \
 --spring.datasource.password=test!1234 \
 --xxl.job.accessToken=123' \
 -p 8080:8080 \
 -v /tmp:/data/applogs \
 --name xxl-job-admin \
 -d xuxueli/xxl-job-admin:{指定版本} 
```

更多配置内容可以查看：

`/xxl-job/xxl-job-admin/src/main/resources/application.properties`

值得注意的是，**数据库密码中如果包含特殊字符(例如，& 或 ！)，需要对特殊字符进行转义，PARAMS 参数值一定要使用使用单引号而不能使用双引号。**

| 常用转义字符  | 作用                                  |
| ------- | ----------------------------------- |
| 反斜杠（\）  | 使反斜杠后面的一个变量变为单纯的字符串，如果放在引号里面，是不起作用的 |
| 单引号（’’） | 转义其中所有的变量为单纯的字符串                    |
| 双引号（""） | 保留其中的变量属性，不进行转义处理                   |

调度中心访问地址：

```
http://localhost:8080/xxl-job-admin
```

(该地址执行器将会使用到，作为回调地址)，默认登录账号 “admin/123456”，登录后运行界面如下图所示：

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918140217.png)

**配置执行器**

在application.yml 中加入执行器配置：

```yaml
# XXL-JOB 配置
xxl:
  job:
    admin:
      ### 调度中心部署根地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
      addresses: http://127.0.0.1:8080/xxl-job-admin
    executor:
      ### 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
      appname: xxl-job-executor-novel
      ### 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
      logpath: logs/xxl-job/jobhandler
    ### xxl-job, access token
    accessToken: 123
```

设置配置类：

```java
/**
 * XXL-JOB 配置类
 *
 * @author xiongxiaoyang
 * @date 2022/5/31
 */
@Configuration
@Slf4j
public class XxlJobConfig {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.executor.appname}")
    private String appname;

    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setAppname(appname);
        xxlJobSpringExecutor.setLogPath(logPath);
        return xxlJobSpringExecutor;
    }
}
```

#### 3. 实际应用

使用XXL-JOB实现Elasticsearch数据同步任务。要完成定时任务其实Spring Task也能胜任，但是XXL-JOB明显可以做更多事情，做关键的是它提供一个可视化的界面方便我们进行监控，而且也能在有需求的时候转化为分布式。

```java
public ReturnT<String> saveToEs() {

        try {
            QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
            List<BookInfo> bookInfos;
            long maxId = 0;
            for (; ; ) {
                queryWrapper.clear();
                // 从数据库中获取数据，按照ID升序排列，小说字数大于0，id要大于上一次获取的最大id，每次获取30条
                queryWrapper
                        .orderByAsc(DatabaseConsts.CommonColumnEnum.ID.getName())
                        .gt(DatabaseConsts.CommonColumnEnum.ID.getName(), maxId)
                        .gt(DatabaseConsts.BookTable.COLUMN_WORD_COUNT, 0)
                        .last(DatabaseConsts.SqlEnum.LIMIT_30.getSql());
                bookInfos = bookInfoMapper.selectList(queryWrapper);
                if (bookInfos.isEmpty()) {
                    break;
                }

                // 创建批量请求
                BulkRequest.Builder br = new BulkRequest.Builder();
                // 遍历数据，将数据添加到批量请求中
                for (BookInfo book : bookInfos) {
                    br.operations(op -> op
                            .index(idx -> idx
                                    .index(EsConsts.BookIndex.INDEX_NAME)
                                    .id(book.getId().toString())
                                    .document(EsBookDto.build(book))
                            )
                    ).timeout(Time.of(t -> t.time("10s")));
                    maxId = book.getId();
                }

                // 执行批量请求，并获取响应
                BulkResponse result = elasticsearchClient.bulk(br.build());

                // 检查响应是否有错误
                if (result.errors()) {
                    log.error("Bulk had errors");
                    for (BulkResponseItem item : result.items()) {
                        if (item.error() != null) {
                            log.error(item.error().reason());
                        }
                    }
                }
            }
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }
```

### AMQP

#### 1. 简单介绍

AMQP，即 Advanced Message Queuing Protocol，一个提供统一消息服务的应用层标准 **高级消息队列协议**（二进制应用层协议），是应用层协议的一个开放标准，为面向消息的中间件设计，兼容 JMS。基于此协议的客户端与消息中间件可传递消息，并不受客户端/中间件同产品，不同的开发语言等条件的限制。**RabbitMQ 就是基于 AMQP 协议实现的。**

而所谓消息队列，就是一个存放消息的容器，当我们需要使用消息的时候，直接从容器中取出消息供自己使用即可。由于队列 Queue 是一种先进先出的数据结构，所以消费消息时也是按照顺序来消费的。参与消息传递的双方称为 **生产者** 和 **消费者** ，生产者负责发送消息，消费者负责处理消息。使用消息队列，一般能有下面三点好处：

- **通过异步处理提高系统性能（减少响应所需时间）**

- **削峰/限流**

- **降低系统耦合性**

JMS中有两种经典的消息模型：

- **点到点（P2P）模型**
  
  ![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827112206.png)
  
  使用队列（Queue）作为消息通信载体；满足生产者与消费者模式，一条消息只能被一个消费者使用，未被消费的消息在队列中保留直到被消费或超时。比如：我们生产者发送 100 条消息的话，两个消费者来消费一般情况下两个消费者会按照消息发送的顺序各自消费一半（也就是你一个我一个的消费。）

- **发布/订阅（Pub/Sub）模型**
  
  ![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827112401.png)
  
  发布订阅模型（Pub/Sub） 使用主题（Topic）作为消息通信载体，类似于广播模式；发布者发布一条消息，该消息通过主题传递给所有的订阅者，**在一条消息广播之后才订阅的用户则是收不到该条消息的**。

#### 2. RabbitMQ

RabbitMQ 整体上是一个生产者与消费者模型，主要负责接收、存储和转发消息。可以把消息传递的过程想象成：当你将一个包裹送到邮局，邮局会暂存并最终将邮件通过邮递员送到收件人的手上，RabbitMQ 就好比由邮局、邮箱和邮递员组成的一个系统。从计算机术语层面来说，RabbitMQ 模型更像是一种交换机模型。RabbitMQ 的整体模型架构如下：

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827112650.png)

**Producer(生产者) 和 Consumer(消费者)**

**Producer(生产者)**：生产消息的一方（邮件投递者）

**Consumer(消费者)**：消费消息的一方（邮件收件人）

消息一般由 2 部分组成：**消息头**（或者说是标签 Label）和 **消息体**。消息体也可以称为 payLoad，消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括 routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等。生产者把消息交由 RabbitMQ 后，RabbitMQ 会根据消息头把消息发送给感兴趣的 Consumer(消费者)。

**Exchange(交换器)**

在 RabbitMQ 中，消息并不是直接被投递到 **Queue(消息队列)** 中的，中间还必须经过 **Exchange(交换器)** 这一层，**Exchange(交换器)** 会把我们的消息分配到对应的 **Queue(消息队列)** 中。

**Exchange(交换器)** 用来接收生产者发送的消息并将这些消息路由给服务器中的队列中，如果路由不到，或许会返回给 **Producer(生产者)** ，或许会被直接丢弃掉 。这里可以将 RabbitMQ 中的交换器看作一个简单的实体。

**RabbitMQ 的 Exchange(交换器) 有 4 种类型，不同的类型对应着不同的路由策略**：**direct(默认)**，**fanout**, **topic**, 和 **headers**，不同类型的 Exchange 转发消息的策略有所区别。这个会在介绍 **Exchange Types(交换器类型)** 的时候介绍到。

生产者将消息发给交换器的时候，一般会指定一个 **RoutingKey(路由键)**，用来指定这个消息的路由规则，而这个 **RoutingKey 需要与交换器类型和绑定键(BindingKey)联合使用才能最终生效**。

RabbitMQ 中通过 **Binding(绑定)** 将 **Exchange(交换器)** 与 **Queue(消息队列)** 关联起来，在绑定的时候一般会指定一个 **BindingKey(绑定建)**，这样 RabbitMQ 就知道如何正确将消息路由到队列了，如下图所示。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。Exchange 和 Queue 的绑定可以是多对多的关系。

Binding(绑定)示意图如下：

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827141108.png)

生产者将消息发送给交换器时，需要一个 RoutingKey，当 BindingKey 和 RoutingKey 相匹配时，消息会被路由到对应的队列中。在绑定多个队列到同一个交换器的时候，这些绑定允许使用相同的 BindingKey。BindingKey 并不是在所有的情况下都生效，它依赖于交换器类型，比如 fanout 类型的交换器就会无视，而是将消息路由到所有绑定到该交换器的队列中。

**Queue(消息队列)**

**Queue(消息队列)** 用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。

**RabbitMQ** 中消息只能存储在 **队列** 中，这一点和 **Kafka** 这种消息中间件相反。Kafka 将消息存储在 **topic（主题）** 这个逻辑层面，而相对应的队列逻辑只是 topic 实际存储文件中的位移标识。 RabbitMQ 的生产者生产消息并最终投递到队列中，消费者可以从队列中获取消息并消费。

**多个消费者可以订阅同一个队列**，这时队列中的消息会被平均分摊（Round-Robin，即轮询）给多个消费者进行处理，而不是每个消费者都收到所有的消息并处理，这样避免消息被重复消费。

**RabbitMQ** 不支持队列层面的广播消费，如果有广播消费的需求，需要在其上进行二次开发,这样会很麻烦，不建议这样做。

**Broker(消息中间件的服务节点)**

对于 RabbitMQ 来说，一个 RabbitMQ Broker 可以简单地看作一个 RabbitMQ 服务节点，或者 RabbitMQ 服务实例。大多数情况下也可以将一个 RabbitMQ Broker 看作一台 RabbitMQ 服务器。

下图展示了生产者将消息存入 RabbitMQ Broker,以及消费者从 Broker 中消费数据的整个流程。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827141440.png)

**Exchange Types(交换器类型)**

RabbitMQ 常用的 Exchange Type 有 **fanout**、**direct**、**topic**、**headers** 这四种（AMQP 规范里还提到两种 Exchange Type，分别为 system 与 自定义，这里不予以描述）。

**①fanout**

fanout 类型的 Exchange 路由规则非常简单，它会把所有发送到该 Exchange 的消息路由到所有与它绑定的 Queue 中，不需要做任何判断操作，所以 fanout 类型是所有的交换机类型里面速度最快的。fanout 类型常用来广播消息。

**②direct**

direct 类型的 Exchange 路由规则也很简单，它会把消息路由到那些 Bindingkey 与 RoutingKey 完全匹配的 Queue 中。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827141617.png)

以上图为例，如果发送消息的时候设置路由键为“warning”,那么消息会路由到 Queue1 和 Queue2。如果在发送消息的时候设置路由键为"Info”或者"debug”，消息只会路由到 Queue2。如果以其他的路由键发送消息，则消息不会路由到这两个队列中。

direct 类型常用在处理有优先级的任务，根据任务的优先级把消息发送到对应的队列，这样可以指派更多的资源去处理高优先级的队列。

**③topic**

前面讲到 direct 类型的交换器路由规则是完全匹配 BindingKey 和 RoutingKey ，但是这种严格的匹配方式在很多情况下不能满足实际业务的需求。topic 类型的交换器在匹配规则上进行了扩展，它与 direct 类型的交换器相似，也是将消息路由到 BindingKey 和 RoutingKey 相匹配的队列中，但这里的匹配规则有些不同，它约定：

- RoutingKey 为一个点号“ . ”分隔的字符串（被点号“ . ”分隔开的每一段独立的字符串称为一个单词），如 “com.rabbitmq.client”、“java.util.concurrent”、“com.hidden.client”;
- BindingKey 和 RoutingKey 一样也是点号“ . ”分隔的字符串；
- BindingKey 中可以存在两种特殊字符串“ * ”和“ # ”，用于做模糊匹配，其中“ * ”用于匹配一个单词，“ # ”用于匹配多个单词(可以是零个)。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827141737.png)

以上图为例：

- 路由键为 “com.rabbitmq.client” 的消息会同时路由到 Queue1 和 Queue2;
- 路由键为 “com.hidden.client” 的消息只会路由到 Queue2 中；
- 路由键为 “com.hidden.demo” 的消息只会路由到 Queue2 中；
- 路由键为 “java.rabbitmq.demo” 的消息只会路由到 Queue1 中；
- 路由键为 “java.util.concurrent” 的消息将会被丢弃或者返回给生产者（需要设置 mandatory 参数），因为它没有匹配任何路由键。

**④headers(不推荐)**

headers 类型的交换器不依赖于路由键的匹配规则来路由消息，而是根据发送的消息内容中的 headers 属性进行匹配。在绑定队列和交换器时指定一组键值对，当发送消息到交换器时，RabbitMQ 会获取到该消息的 headers（也是一个键值对的形式)，对比其中的键值对是否完全匹配队列和交换器绑定时指定的键值对，如果完全匹配则消息会路由到该队列，否则不会路由到该队列。headers 类型的交换器性能会很差，而且也不实用，基本上不会看到它的存在。

#### 3. 实际应用

在novel分布式环境中，数据库的小说信息可能在多个地方保留副本。比如在Caffeine，Redis以及Elasticsearch中。当小说信息发生变更，我们需要通知所有副本数据进行更新，这时就可以使用消息队列了。

首先需要在RabbiMQ 的 web 管理界面，创建虚拟主机`novel`。RabbitMQ的virtual host是一个逻辑上的概念，它允许在一个RabbitMQ实例上运行多个独立的应用程序。每个virtual host拥有自己的队列、交换器、绑定、用户权限等资源，这些资源之间是相互隔离的。

然后创建AMQP配置类，配置交换机、队列以及绑定关系。

```java
   /**
     * 小说信息改变交换机
     */
    @Bean
    public FanoutExchange bookChangeExchange() {
        // Fanout是广播模式，会将消息发送到所有绑定到该交换机的队列中
        // 传入的参数是交换机的名称
        return new FanoutExchange(AmqpConsts.BookChangeMq.EXCHANGE_NAME);AME);
    }

    /**
     * Elasticsearch book 索引更新队列
     */
    @Bean
    public Queue esBookUpdateQueue() {
        return new Queue(AmqpConsts.BookChangeMq.QUEUE_ES_UPDATE);
    }

    /**
     * Elasticsearch book 索引更新队列绑定到小说信息改变交换机
     */
    @Bean
    public Binding esBookUpdateQueueBinding() {
        // 绑定队列到交换机
        // 如果是with方法就是指定routingKey，这里不需要
        return BindingBuilder.bind(esBookUpdateQueue()).to(bookChangeExchange());
    }
```

接着创建一个AMQP消息管理类，用来发送各种AMQP消息。

```java
    /**
     * AmqpTemplate是Spring AMQP中的一个接口，它提供了发送和接收AMQP消息的操作
     */
    private final AmqpTemplate amqpTemplate;

    @Value("${spring.amqp.enabled:false}")
    private boolean amqpEnabled;

    /**
     * 发送小说信息改变消息
     */
    public void sendBookChangeMsg(Long bookId) {
        if (amqpEnabled) {
            sendAmqpMessage(amqpTemplate, AmqpConsts.BookChangeMq.EXCHANGE_NAME, null, bookId);
        }
    }

    private void sendAmqpMessage(AmqpTemplate amqpTemplate, String exchange, String routingKey, Object message) {
        // 如果当前在事务中，则在事务执行完成后再发送，否则可以直接发送
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            // 调用模板的convertAndSend方法，将消息发送到指定的exchange和routingKey
                            amqpTemplate.convertAndSend(exchange, routingKey, message);
                        }
                    });
            return;
        }
        amqpTemplate.convertAndSend(exchange, routingKey, message);
    }
```

其次，我们看看如何在更新小说信息之后，通过上面的代码来发送消息。值得注意的是，小说信息更新肯定是以事务的形式来更新，所以上文中的代码才需要等待事务完成后才能发送消息。

```java
    @Transactional
    @Override
    public RestResp<Void> updateBookChapter(Long chapterId, ChapterUpdateReqDto dto) {
        // 1.查询章节信息
        BookChapterRespDto chapter = bookChapterCacheManager.getChapter(chapterId);
        // 2.查询小说信息
        BookInfoRespDto bookInfo = bookInfoCacheManager.getBookInfo(chapter.getBookId());
        // 3.更新章节信息
        BookChapter newChapter = new BookChapter();
        newChapter.setId(chapterId);
        newChapter.setChapterName(dto.getChapterName());
        newChapter.setWordCount(dto.getChapterContent().length());
        newChapter.setIsVip(dto.getIsVip());
        newChapter.setUpdateTime(LocalDateTime.now());
        bookChapterMapper.updateById(newChapter);
        // 4.更新章节内容
        BookContent newContent = new BookContent();
        newContent.setContent(dto.getChapterContent());
        newContent.setUpdateTime(LocalDateTime.now());
        QueryWrapper<BookContent> bookContentQueryWrapper = new QueryWrapper<>();
        bookContentQueryWrapper.eq(DatabaseConsts.BookContentTable.COLUMN_CHAPTER_ID, chapterId);
        bookContentMapper.update(newContent, bookContentQueryWrapper);
        // 5.更新小说信息
        BookInfo newBookInfo = new BookInfo();
        newBookInfo.setId(chapter.getBookId());
        newBookInfo.setUpdateTime(LocalDateTime.now());
        newBookInfo.setWordCount(bookInfo.getWordCount() - chapter.getChapterWordCount() + dto.getChapterContent().length());
        if (Objects.equals(bookInfo.getLastChapterId(), chapterId)) {
            // 更新最新章节信息
            newBookInfo.setLastChapterName(dto.getChapterName());
            newBookInfo.setLastChapterUpdateTime(LocalDateTime.now());
        }
        bookInfoMapper.updateById(newBookInfo);
        // 6.清理章节信息缓存
        bookChapterCacheManager.evictBookChapterCache(chapterId);
        // 7.清理章节内容缓存
        bookContentCacheManager.evictBookContentCache(chapterId);
        // 8.清理小说信息缓存
        bookInfoCacheManager.evictBookInfoCache(chapter.getBookId());
        // 9.发送小说信息更新的 MQ 消息
        amqpMsgManager.sendBookChangeMsg(chapter.getBookId());
        return RestResp.ok();
    }
```

最后，我们还需要监听消息是否到来，如果消息到来，那么对其进行处理。这里我们创建 Rabbit 队列监听器，监听各个 RabbitMQ 队列的消息并处理。

```java
    /**
     * 监听小说信息改变的ES更新队列，更新最新小说信息到ES
     * RabbitListener注解是让Spring监听指定的队列，当队列中有消息时，就会触发监听器进行处理
     */
    @RabbitListener(queues = AmqpConsts.BookChangeMq.QUEUE_ES_UPDATE)
    @SneakyThrows
    public void updateEsBook(Long bookId) {
        BookInfo bookInfo = bookInfoMapper.selectById(bookId);
        // 将最新的小说信息更新到ES，调用ElasticsearchClient的index方法将小说信息索引到ES
        IndexResponse response = elasticsearchClient.index(i -> i
                .index(EsConsts.BookIndex.INDEX_NAME)
                .id(bookInfo.getId().toString())
                .document(EsBookDto.build(bookInfo)));
        log.info("Indexed with version " + response.version());
    }
```

### Redisson

#### 1. 简单介绍

首先我们得介绍一下分布式锁是什么。

在多线程环境中，如果多个线程同时访问共享资源，会发生数据竞争，可能会导致出现脏数据或者系统问题，威胁到程序的正常运行，比如商品超卖的现象。在单机多线程的环境下，我们可以使用`ReetrantLock` 类、`synchronized` 关键字这类 JDK 自带的 **本地锁** 来控制一个 JVM 进程内的多个线程对本地共享资源的访问。

而在分布式系统中，不同的服务/客户端通常运行在独立的JVM进程上，如果多个JVM进程共享同一份资源的话，那么使用本地锁就无法实现资源的互斥访问了。于是，分布式锁就诞生了。

举个例子：系统的订单服务一共部署了 3 份，都对外提供服务。用户下订单之前需要检查库存，为了防止超卖，这里需要加锁以实现对检查库存操作的同步访问。由于订单服务位于不同的 JVM 进程中，本地锁在这种情况下就没办法正常工作了。我们需要用到分布式锁，这样的话，即使多个线程不在同一个 JVM 进程中也能获取到同一把锁，进而实现共享资源的互斥访问。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827174832.png)

一个基本的分布式锁需要满足：

- **互斥**：任意一个时刻，锁只能被一个线程持有。
- **高可用**：锁服务是高可用的，当一个锁服务出现问题，能够自动切换到另外一个锁服务。并且，即使客户端的释放锁的代码逻辑出现问题，锁最终一定还是会被释放，不会影响其他线程对共享资源的访问。这一般是通过超时机制实现的。
- **可重入**：一个节点获取了锁之后，还可以再次获取锁。

除了上面这三个基本条件之外，一个好的分布式锁还需要满足下面这些条件：

- **高性能**：获取和释放锁的操作应该快速完成，并且不应该对整个系统的性能造成过大影响。
- **非阻塞**：如果获取不到锁，不能无限期等待，避免对系统正常运行造成影响。

#### 2. 快速上手

假如我们想要基于Redis实现一个最简易的分布式锁，那么关键还是在于**互斥**。基本步骤为：

- 使用 SETNX 命令尝试设置锁，成功则获得锁。

- 对共享资源进行操作。

- 释放锁，使用 DEL 命令删除锁。

在 Redis 中， `SETNX` 命令是可以帮助我们实现互斥。`SETNX` 即 **SET** if **N**ot e**X**ists (对应 Java 中的 `setIfAbsent` 方法)，如果 key 不存在的话，才会设置 key 的值。如果 key 已经存在， `SETNX` 啥也不做。

```shell
> SETNX lockKey uniqueValue
(integer) 1
> SETNX lockKey uniqueValue
(integer) 0
```

释放锁的话，直接通过 `DEL` 命令删除对应的 key 即可。

```shell
> DEL lockKey
(integer) 1
```

为了防止误删到其他的锁（**误删锁问题**），我们需要使用 Lua 脚本通过 key 对应的 value（唯一值）来判断。选用 Lua 脚本是为了保证解锁操作的原子性。因为 Redis 在执行 Lua 脚本时，可以以原子性的方式执行，从而保证了锁释放操作的原子性。

```lua
// 释放锁时，先比较锁对应的 value 值是否相等，避免锁的误释放
if redis.call("get",KEYS[1]) == ARGV[1] then
    return redis.call("del",KEYS[1])
else
    return 0
end
```

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230827182130.png)

这是一种最简易的 Redis 分布式锁实现，实现方式比较简单，性能也很高效。不过，这种方式实现分布式锁存在一些问题。就比如应用程序遇到一些问题比如释放锁的逻辑突然挂掉，可能会**导致锁无法被释放**，进而造成共享资源无法再被其他线程/进程访问。

为了避免出现死锁导致其他线程无法获取锁，我们可以给锁设置一个过期时间来解决这个问题（**锁过期问题**）。

```shell
> SET lockKey uniqueValue EX 3 NX
OK
```

- **lockKey**：加锁的锁名；
- **uniqueValue**：能够唯一标识锁的随机字符串；
- **NX**：只有当 lockKey 对应的 key 值不存在的时候才能 SET 成功；
- **EX**：过期时间设置（秒为单位）EX 3 标示这个锁有一个 3 秒的自动过期时间。与 EX 对应的是 PX（毫秒为单位），这两个都是过期时间设置。

**一定要保证设置指定 key 的值和过期时间是一个原子操作！！！** 不然的话，依然可能会出现锁无法被释放的问题。以上做法确实解决了问题，不过，新的问题接踵而至：**如果操作共享资源的时间大于过期时间，就会出现锁提前过期的问题，进而导致分布式锁直接失效。如果锁的超时时间设置过长，又会影响到性能。** 这又涉及到了另一个问题，即**锁续期问题**。

关于锁续期，可重入锁，公平锁/非公平锁等更高级的分布式锁，可以直接使用Redisson来实现。

#### 3. 实际应用

而Redisson是一个具有内存数据网格(In-Memory Data Grid)功能的Redis Java客户端。它提供了更方便、最简单的使用Redis的方法。Redisson对象提供了关注点分离，使您能够将注意力集中在数据建模和应用程序逻辑上。Redisson提供了现成的分布式锁实现，因此我们无需自己实现分布式锁。

为了方便使用，我们构建@Key和@Lock这两个注解。这里说一下自定义注解中一些注解的含义。

- **@Documented** 用于标注该自定义注解，使其在生成的API文档中显示出来
- **@Retention(RUNTIME)** 意味着该注解在运行时可用
- **@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})** 意味着这个注解可以应用于方法、成员变量和参数

```java
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Key {
    // 分布式锁的key值，默认为
    String expr() default "";
}
```

```java
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Lock {        
    // 锁的前缀，能唯一标识锁
    String prefix();
    // 是否等待，默认为否
    boolean isWait() default false;
    // 等待时间，默认为3L
    long waitTime() default 3L;
    // 错误代码，默认OK
    ErrorCodeEnum failCode() default ErrorCodeEnum.OK;
}
```

然后使用**AspectJ**，使得我们对某个方法添加注解时，能对其上锁。我们先来讲一下AOP，AOP（Aspect Oriented Programming）实际上就是：在运行时，动态地将代码切入到类的指定方法、指定位置上。也就是说，我们可以使用AOP来帮助我们在方法执行前或执行之后，做一些额外的操作，实际上，它就是代理。这样做的好处就是，通过AOP我们可以在保证原有业务不变的情况下，添加额外的动作，比如我们的某些方法执行完成之后，需要打印日志，那么这个时候，我们就可以使用AOP来帮助我们完成，它可以批量地为这些方法添加动作。可以说，它相当于将我们原有的方法，在不改变源代码的基础上进行了增强处理。

AOP 切面编程设计到的一些专业术语：

| 术语             | 含义                                   |
| -------------- | ------------------------------------ |
| 目标(Target)     | 被通知的对象                               |
| 代理(Proxy)      | 向目标对象应用通知之后创建的代理对象                   |
| 连接点(JoinPoint) | 目标对象的所属类中，定义的所有方法均为连接点               |
| 切入点(Pointcut)  | 被切面拦截 / 增强的连接点（切入点一定是连接点，连接点不一定是切入点） |
| 通知(Advice)     | 增强的逻辑 / 代码，也即拦截到目标对象的连接点之后要做的事情      |
| 切面(Aspect)     | 切入点(Pointcut)+通知(Advice)             |
| Weaving(织入)    | 将通知应用到目标对象，进而生成代理对象的过程动作             |

而**AspectJ**则是Java生态系统中最完整的AOP框架之一，不同于**Spring AOP 属于运行时增强，AspectJ是编译时增强。** Spring AOP 基于代理(Proxying)，而 AspectJ 基于字节码操作(Bytecode Manipulation)。AspectJ 定义的通知类型一般有：

- **Before**（前置通知）：目标对象的方法调用之前触发。
- **After** （后置通知）：目标对象的方法调用之后触发。
- **AfterReturning**（返回通知）：目标对象的方法调用完成，在返回结果值之后触发。
- **AfterThrowing**（异常通知）：目标对象的方法运行中抛出 / 触发异常后触发。AfterReturning 和 AfterThrowing 两者互斥。如果方法调用成功无异常，则会有返回值；如果方法抛出了异常，则不会有返回值。
- **Around** （环绕通知）：编程式控制目标对象的方法调用。环绕通知是所有通知类型中可操作范围最大的一种，因为它可以直接拿到目标对象，以及要执行的方法，所以环绕通知可以任意的在目标对象的方法调用前后搞事，甚至不调用目标对象的方法。

分布式锁的切面代码如下：

```java
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {
    private final RedissonClient redissonClient;
    private static final String KEY_PREFIX = "Lock";
    private static final String KEY_SEPARATOR = "::";

    /**
     * Around注解的value属性指定了该通知应用于哪些方法，即带有@Lock注解的方法
     * 而SneakyThrows注解可以在不声明throw子句的情况下抛出已检查的异常
     */
    @Around(value = "@annotation(io.github.yeyuhl.novel.core.annotation.Lock)")
    @SneakyThrows
    public Object doAround(ProceedingJoinPoint pjp) {
        // 获取目标方法和@Lock注解对象
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = methodSignature.getMethod();
        Lock lock = targetMethod.getAnnotation(Lock.class);
        // 调用buildLockKey方法构建锁的Key
        String lockKey = KEY_PREFIX + buildLockKey(lock.prefix(), targetMethod, pjp.getArgs());
        // 使用getLock方法获取锁对象
        RLock rLock = redissonClient.getLock(lockKey);
        // 查看lock是否要等待，如果要等待就在lock.waitTime()时间内尝试获取锁，如果这个时间内获取不到则返回false
        // 如果不等待就直接尝试获取锁，如果获取不到则返回false
        if (lock.isWait() ? rLock.tryLock(lock.waitTime(), TimeUnit.SECONDS) : rLock.tryLock()) {
            // 如果上锁成功
            try {
                // 调用proceed方法来执行被代理对象的方法，即targetMethod
                return pjp.proceed();
            } finally {
                // 执行完后释放锁
                rLock.unlock();
            }
        }
        // 如果上锁失败就则抛出一个业务异常
        throw new BusinessException(lock.failCode());
    }

    /**
     * 根据@Lock注解中的prefix、@Lock作用的目标方法及其参数构建锁的Key
     *
     * @param prefix 分布式锁唯一标识
     * @param method 目标方法对象
     * @param args   方法参数值数组
     * @return 返回Key字符串
     */
    private String buildLockKey(String prefix, Method method, Object[] args) {
        StringBuilder builder = new StringBuilder();
        // 如果有前缀，在分割符"::"后面添加前缀
        if (StringUtils.hasText(prefix)) {
            builder.append(KEY_SEPARATOR).append(prefix);
        }
        // 遍历目标方法的所有参数
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            builder.append(KEY_SEPARATOR);
            // 检查每个参数是否有@Key注解
            if (parameters[i].isAnnotationPresent(Key.class)) {
                // 如果有，则使用注解中的表达式和参数值来构建Key的一部分
                Key key = parameters[i].getAnnotation(Key.class);
                builder.append(parseKeyExpr(key.expr(), args[i]));
            }
        }
        return builder.toString();
    }

    /**
     * 用于解析@Key注解中的表达式
     *
     * @param expr 表达式的字符串
     * @param arg  方法参数的值
     * @return 返回一个字符串
     */
    private String parseKeyExpr(String expr, Object arg) {
        // 表达字符串为空，则直接返回参数值的字符串表示
        if (!StringUtils.hasText(expr)) {
            return arg.toString();
        }
        // SpEL是Spring表达式语言的缩写，它支持非常多的语法
        ExpressionParser parser = new SpelExpressionParser();
        // 使用TemplateParserContext 来支持#{...}形式的表达式
        Expression expression = parser.parseExpression(expr, new TemplateParserContext());
        return expression.getValue(arg, String.class);
    }
}
```

在这个项目中，我们需要对saveComment上分布式锁，这是因为会有多个用户同时对某一本小说进行评论，如果不上锁，则只有一个用户的评论会被保存到数据库中，其他用户的评论就会丢失，这是不能接受的。

```java
    @Lock(prefix = "userComment")
    @Override
    public RestResp<Void> saveComment(@Key(expr = "#{userId + '::' + bookId}") UserCommentReqDto dto) {
    }
```

### 认证授权

#### 1. 简单介绍

系统权限控制最常采用的访问控制模型就是 **RBAC 模型** 。RBAC 即基于角色的权限访问控制（Role-Based Access Control）。这是一种通过角色关联权限，角色同时又关联用户的授权的方式。简单地说：一个用户可以拥有若干角色，每一个角色又可以被分配若干权限，这样就构造成“用户-角色-权限” 的授权模型。在这种模型中，用户与角色、角色与权限之间构成了多对多的关系。

传统方案一般使用**Session-Cookie**方案进行身份认证。众所周知，**HTTP**是一个**无状态协议**。所谓无状态指：**服务器向客户发送被请求的文件，而不存储任何关于该客户的状态信息**。但我们有时希望我们能识别到连接的用户，又或者是希望把内容与用户身份联系起来。因此就有了Cookie，Cookie是某些网站为了辨别用户身份而储存在用户本地终端上的数据（通常经过加密）。而Session的主要作用就是通过服务端记录用户的状态。很多时候我们都是通过 `SessionID`来实现特定的用户，`SessionID`一般会选择存放在Redis中。

其认证流程如下：

1. 用户向服务器发送用户名、密码、验证码用于登陆系统。
2. 服务器验证通过后，服务器为用户创建一个 `Session`，并将 `Session` 信息存储起来。
3. 服务器向用户返回一个 `SessionID`，写入用户的 `Cookie`。
4. 当用户保持登录状态时，`Cookie` 将与每个后续请求一起被发送出去。
5. 服务器可以将存储在 `Cookie` 上的 `SessionID` 与存储在内存中或者数据库中的 `Session` 信息进行比较，以验证用户的身份，返回给用户客户端响应信息的时候会附带用户当前的状态。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230828085358.png)

使用 `Session` 的时候需要注意下面几个点：

- 依赖 `Session` 的关键业务一定要确保客户端开启了 `Cookie`。
- 注意 `Session` 的过期时间。

但Cookie无法防止**CSRF攻击**，所谓CSRF就是跨站请求伪造，攻击者诱导受害者进入第三方网站，在第三方网站中，向被攻击网站发送跨站请求。利用受害者在被攻击网站已经获取的注册凭证，绕过后台的用户验证，达到冒充用户对被攻击的网站执行某项操作的目的。一个典型的CSRF攻击有着如下的流程：

- 受害者登录a.com，并保留了登录凭证（Cookie）。
- 攻击者引诱受害者访问了b.com。
- b.com 向 a.com 发送了一个请求：a.com/act=xx。浏览器会默认携带a.com的Cookie。
- a.com接收到请求后，对请求进行验证，并确认是受害者的凭证，误以为是受害者自己发送的请求。
- a.com以受害者的名义执行了act=xx。
- 攻击完成，攻击者在受害者不知情的情况下，冒充受害者，让a.com执行了自己定义的操作。

使用Cookie进行认证会出现这种问题，但是使用**Token**的话就不会存在这个问题，在我们登录成功获得 `Token` 之后，一般会选择存放在 `localStorage` （浏览器本地存储）中。然后我们在前端通过某些方式会给每个发到后端的请求加上这个 `Token`，这样就不会出现 CSRF 漏洞的问题。因为，即使有个你点击了非法链接发送了请求到服务端，这个非法请求是不会携带 `Token` 的，所以这个请求将是非法的。

如果说在多个应用系统中，我们希望登录一次就能访问所有相互信任的应用系统，不用重复登录，那就需要用到**SSO**。SSO英文全称 Single Sign On，单点登录。比如说我登录了网易账号中心（[https://reg.163.com/](https://reg.163.com/) ）之后访问以下站点都是登录状态：

- 网易博客 [https://blog.163.comopen in new window](https://blog.163.com/)
- 网易花田 [https://love.163.comopen in new window](https://love.163.com/)
- 网易考拉 [https://www.kaola.comopen in new window](https://www.kaola.com/)
- 网易 Lofter [http://www.lofter.comopen in new window](http://www.lofter.com/)

SSO的好处有：

1. **用户角度** :用户能够做到一次登录多次使用，无需记录多套用户名和密码，省心。
2. **系统管理员角度** : 管理员只需维护好一个统一的账号中心就可以了，方便。
3. **新系统开发角度:** 新系统开发时只需直接对接统一的账号中心即可，简化开发流程，省时。

#### 2. JWT

JWT （JSON Web Token） 是目前最流行的跨域认证解决方案，是一种基于 Token 的认证授权机制。 从 JWT 的全称可以看出，JWT 本身也是 Token，一种规范化之后的 JSON 结构的 Token。由于JWT本身包含了身份认证所需的所有信息，因此服务器无需存储额外的Session信息。

JWT 本质上就是一组字串，通过（`.`）切分成三个为 Base64 编码的部分：

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230828091308.png)

- **Header** : 描述 JWT 的元数据，定义了生成签名的算法以及 `Token` 的类型。
  
  - typ(Type)：令牌类型，也就是JWT，亦或者是OAuth 2.0 规范中的Bearer，表示令牌是通过 HTTP 头部来传输的。
  - alg(Algorithm)：签名算法，例如HS256。

- **Payload** : 用来存放实际需要传递的数据。其中包含了Claims(声明，包含 JWT 的相关信息)。Claims分为三种类型：
  
  - **Registered Claims（注册声明）**：预定义的一些声明，建议使用，但不是强制性的。
  - **Public Claims（公有声明）**：JWT 签发方可以自定义的声明，但是为了避免冲突，应该在 [IANA JSON Web Token Registry](https://www.iana.org/assignments/jwt/jwt.xhtml) 中定义它们。
  - **Private Claims（私有声明）**：JWT 签发方因为项目需要而自定义的声明，更符合实际项目场景使用。
  
  常见的注册声明有：
  
  - `iss`（issuer）：JWT 签发方。
  - `iat`（issued at time）：JWT 签发时间。
  - `sub`（subject）：JWT 主题。
  - `aud`（audience）：JWT 接收方。
  - `exp`（expiration time）：JWT 的过期时间。
  - `nbf`（not before time）：JWT 生效时间，早于该定义的时间的 JWT 不能被接受处理。
  - `jti`（JWT ID）：JWT 唯一标识。
  
  注意Payload默认不加密，**不能将隐私信息存在Payload中**。

- **Signature（签名）**：服务器通过 Payload、Header 和一个密钥(存在服务端，不能泄露)使用 Header 里面指定的签名算法（默认是 HMAC SHA256）生成签名，用于对前两部分签名，防止JWT（主要是payload）被篡改。其计算公式如下：
  
  ```
  HMACSHA256(
    base64UrlEncode(header) + "." +
    base64UrlEncode(payload),
    secret)
  ```

JWT 通常是这样的：`xxxxx.yyyyy.zzzzz`。

在基于 JWT 进行身份验证的的应用程序中，服务器通过 Payload、Header 和 Secret(密钥)创建 JWT 并将 JWT 发送给客户端。客户端接收到 JWT 之后，会将其保存在 Cookie 或者 localStorage 里面，以后客户端发出的所有请求都会携带这个令牌。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230828094554.png)

简化后的步骤如下：

1. 用户向服务器发送用户名、密码以及验证码用于登陆系统。
2. 如果用户用户名、密码以及验证码校验正确的话，服务端会返回已经签名的 Token，也就是 JWT。
3. 用户以后每次向后端发请求都在 Header 中带上这个 JWT 。
4. 服务端检查 JWT 并从中获取用户相关信息。

两点建议：

1. 建议将 JWT 存放在 localStorage 中，放在 Cookie 中会有 CSRF 风险。
2. 请求服务端并携带 JWT 的常见做法是将其放在 HTTP Header 的 `Authorization` 字段中（`Authorization: Bearer Token`）。

#### 3. 实际应用

Novel的预定设计中有前台门户系统，平台后台管理系统（虽然目前没有）和作家后台管理系统。除了平台后台管理系统，剩下两个系统我们希望实现SSO。为了避免所有认证授权逻辑都堆积在AuthInterceptor这一个方法中，导致解耦性不高，后续难以维护，我们可以使用策略模式。

策略模式定义了算法族，分别封装起来，让它们之间可以互相替换，此模式让算法的变化独立于使用算法的客户。策略模式的核心在于封装变化，在我们系统中就是定义多个不同系统的认证授权策略（算法族），分别封装成独立的类。拦截器在运行时根据具体的请求 URI 来动态调用相应系统的认证授权算法。当某一系统的认证授权逻辑发生变化或增加新的子系统时，我们只需要修改或增加相应的策略类，而不会影响到其它的策略类（子系统）和客户（拦截器）。

首先创建AuthStrategy接口，该接口定义了一个默认的方法实现用户端所有子系统都需要的统一账号认证逻辑和一个封装各个系统独立认证授权逻辑的待实现方法（例如，作家管理系统还需要验证作家账号是否存在和作家状态是否正常）：

```java
public interface AuthStrategy {
    /**
     * 用户认证授权
     *
     * @param token      登录 token
     * @param requestUri 请求的 URI
     * @throws BusinessException 认证失败则抛出业务异常
     */
    void auth(String token, String requestUri) throws BusinessException;

    /**
     * 前台多系统单点登录统一账号认证授权（门户系统、作家系统）
     * 接口中default关键字的方法定义为默认实现
     *
     * @param jwtUtils             jwt工具
     * @param userInfoCacheManager 用户缓存管理对象
     * @param token                登录token
     * @return 用户ID
     */
    default Long authSSO(JwtUtils jwtUtils, UserInfoCacheManager userInfoCacheManager, String token) {
        if (!StringUtils.hasText(token)) {
            // token为空
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }
        Long userId = jwtUtils.parseToken(token, SystemConfigConsts.NOVEL_FRONT_KEY);
        if (Objects.isNull(userId)) {
            // token解析失败
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }
        UserInfoDto userInfo = userInfoCacheManager.getUser(userId);
        if (Objects.isNull(userInfo)) {
            // 用户不存在
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }
        // 设置userId到当前线程
        UserHolder.setUserId(userId);
        return userId;
    }
}
```

接着在该包下创建各个系统的认证授权策略类，实现上述的 AuthStrategy 接口：

```java
/**
 * 前台门户系统，认证授权策略
 */
public class FrontAuthStrategy implements AuthStrategy{
    private final JwtUtils jwtUtils;
    private final UserInfoCacheManager userInfoCacheManager;
    @Override
    public void auth(String token, String requestUri) throws BusinessException {
        // 统一账号认证
        authSSO(jwtUtils, userInfoCacheManager, token);
    }
}
```

```java
/**
 * 作家后台管理系统，认证授权策略
 */
public class AuthorAuthStrategy implements AuthStrategy {
    private final JwtUtils jwtUtils;
    private final UserInfoCacheManager userInfoCacheManager;
    private final AuthorInfoCacheManager authorInfoCacheManager;

    /**
     * 不需要进行作家认证的URI
     */
    private static final List<String> EXCLUDE_URI = List.of(
            ApiRouterConsts.API_AUTHOR_URL_PREFIX + "/register",
            ApiRouterConsts.API_AUTHOR_URL_PREFIX + "/status"
    );

    @Override
    public void auth(String token, String requestUri) throws BusinessException {
        // 统一账号认证
        Long userId = authSSO(jwtUtils, userInfoCacheManager, token);
        if (EXCLUDE_URI.contains(requestUri)) {
            // 该请求不需要进行作家权限认证
            return;
        }

        AuthorInfoDto authorInfoDto = authorInfoCacheManager.getAuthor(userId);
        if (Objects.isNull(authorInfoDto)) {
            // 作家账号不存在，无权访问作家专区
            throw new BusinessException(ErrorCodeEnum.USER_UN_AUTH);
        }

        // 设置作家ID到当前线程
        UserHolder.setAuthorId(authorInfoDto.getId());
    }
}
```

最后在认证拦截器中根据请求 URI 动态调用相应策略：

```java
public class AuthInterceptor implements HandlerInterceptor {
    private final Map<String, AuthStrategy> authStrategy;
    private final ObjectMapper objectMapper;

    /**
     * handle执行前调用
     * SuppressWarnings注解用于抑制编译器产生空值警告
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取登录token
        String token = request.getHeader(SystemConfigConsts.HTTP_AUTH_HEADER_NAME);
        // 获取请求的URI
        String requestUri = request.getRequestURI();
        // 根据请求的URI来得到认证策略，由于子URI在前缀后面，因此需要截取
        String subUri = requestUri.substring(ApiRouterConsts.API_URL_PREFIX.length() + 1);
        String systemName = subUri.substring(0, subUri.indexOf("/"));
        String authStrategyName = String.format("%sAuthStrategy", systemName);

        //开始认证
        try {
            // 调用对应的认证策略
            authStrategy.get(authStrategyName).auth(token, requestUri);
            // 调用父类的preHandle方法，成功则返回true
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } catch (BusinessException e) {
            // 认证失败，返回false，并且JSON响应包含错误信息
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(RestResp.fail(e.getErrorCodeEnum())));
            return false;
        }
    }

    /**
     * handler执行后调用，出现异常不调用
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * DispatcherServlet完全处理完请求后调用，出现异常照常调用
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理当前线程保存的用户数据
        UserHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
```

另外提一嘴如何生成JWT，以及JWT应用于何处。JWT的生成代码如下：

```java
public class JwtUtils {
    /**
     * 注入JWT加密密钥
     */
    @Value("${novel.jwt.secret}")
    private String secret;

    /**
     * 定义系统标识头常量
     */
    private static final String HEADER_SYSTEM_KEY = "systemKeyHeader";

    /**
     * 根据用户ID生成JWT，使用HMAC-SHA算法和密钥进行签名
     *
     * @param uid       用户ID
     * @param systemKey 系统标识，用于区分不同系统的JWT，比如说是前台系统还是后台系统
     * @return JWT
     */
    public String generateToken(Long uid, String systemKey) {
        return Jwts.builder()
                // 自定义设置JWT头部信息
                .setHeaderParam(HEADER_SYSTEM_KEY, systemKey)
                // 设置JWT主题
                .setSubject(uid.toString())
                // 设置签名，使用HMAC-SHA算法和密钥进行签名
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /**
     * 解析JWT返回用户ID
     *
     * @param token     JWT
     * @param systemKey 系统标识
     * @return 用户ID
     */
    public Long parseToken(String token, String systemKey) {
        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            // 判断该 JWT 是否属于指定系统
            if (Objects.equals(claimsJws.getHeader().get(HEADER_SYSTEM_KEY), systemKey)) {
                return Long.parseLong(claimsJws.getBody().getSubject());
            }
        } catch (JwtException e) {
            log.warn("JWT解析失败:{}", token);
        }
        return null;
    }

}
```

而JWT一般用于UserService中的注册和登录，注册和登录成功生成一个JWT给用户，而用户此后每次发送请求，都会附带这个JWT。而服务器就会在TokenParseInterceptor中解析这个JWT并保存，用来分析用户是否购买了付费章节，是否有权观看。

### 解决XXS攻击

#### 1. 简单介绍

上面介绍了CSRF，是从外部干涉所发起的攻击。而 **XSS（跨站脚本攻击）** 则是从内部发起的攻击，也是目前最普遍的Web应用安全漏洞。攻击者通过在合法网站中注入恶意脚本代码来攻击用户。当用户访问受到注入攻击的页面时，恶意代码会在用户的浏览器中执行，从而导致攻击者能够窃取用户的敏感信息、诱导用户操作、甚至控制用户的账号。比如说服务器对用户提交的数据过滤不严，导致浏览器把用户的输入当成js代码并直接返回给客户端执行，从而实现对客户端的攻击目的。

XSS攻击常见的方式有三种：

1. 存储型XSS攻击：攻击者将恶意代码存储到目标网站的数据库中，当其他用户访问包含恶意代码的页面时，恶意代码会被执行。
2. 反射型XSS攻击：攻击者将恶意代码嵌入到URL中，当用户点击包含恶意代码的URL时，恶意代码会被执行。
3. DOM-based XSS攻击：攻击者利用前端JavaScript代码的漏洞，通过修改页面的DOM结构来执行恶意代码。

#### 2. 实际应用

常见的避免XSS攻击的方式就是**过滤掉请求中存在XSS攻击风险的可疑字符串**。比如使用装饰器模式解决表单形式传参的XSS攻击。Spring MVC 是通过 HttpServletRequest 的 getParameterValues 方法来获取用户端的请求参数并绑定到我们 @RequestMapping 方法定义的对象上。所以我们可以装饰 HttpServletRequest 对象，在 getParameterValues 方法里加上自己的行为（对请求参数值里面的特殊字符进行转义）来解决 XSS 攻击。

```java
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static final Map<String, String> REPLACE_RULE = new HashMap<>();

    static {
        REPLACE_RULE.put("<", "&lt");
        REPLACE_RULE.put(">", "&gt");
    }

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 重写getParameterValues方法，将参数名和参数值都做xss过滤
     */
    @Override
    public String[] getParameterValues(String name) {
        // 获取参数
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapeValues = new String[length];
            // 遍历参数，并且用相应的替换字符串替换掉REPLACE_RULE映射中出现的任何字符
            for (int i = 0; i < length; i++) {
                escapeValues[i] = values[i];
                int index = i;
                // 对于escapeValues[index]中字符串，如果包含有REPLACE_RULE中的k，那么就用REPLACE_RULE中的v替换掉
                REPLACE_RULE.forEach((k, v) -> escapeValues[index] = escapeValues[index].replaceAll(k, v));
            }
            // 返回替换后的字符串数组
            return escapeValues;
        }
        return new String[0];
    }
}
```

这里之所以将 **<** 替换成 **&lt** ， **>** 替换成 **&gt** ，是因为在HTML中<>可以用来表示各种标签，攻击者可以构造恶意的标签来攻击用户，比如说：

```html
<script>alert("Hello, world!");</script>
```

当用户在浏览包含该标签的HTML页面的时，浏览器就会执行标签中的JS代码，弹出一个窗口。

但是在前后端分离项目，对于 POST 和 PUT 类型的请求方法，后端基本都是通过 @RequestBody 注解接收 application/json 格式的请求数据，所以以前通过过滤器 + 装饰器 HttpServletRequestWrapper 来解决 XSS 攻击的方式并不适用。在 Spring Boot 中，我们可以通过配置全局的 Json 反序列化器转义特殊字符来解决 XSS 攻击。

```java
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return jsonParser.getValueAsString()
                    .replace("<", "&lt")
                    .replace(">", "&gt");
        }
```

当然，我们还需要创建XSS过滤器。

```java
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (handleExcludeUrl(req)) {
            // 如果是排除的url，直接放行
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // 否则进行过滤
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        filterChain.doFilter(xssRequest, servletResponse);
    }

    private boolean handleExcludeUrl(HttpServletRequest request) {
        // 查看是否有要排除的url
        if (CollectionUtils.isEmpty(xssProperties.excludes())) {
            return false;
        }
        // 获取请求的url
        String url = request.getServletPath();
        // 遍历排除的url
        for (String pattern : xssProperties.excludes()) {
            // 将pattern和"^"作为正则表达式，编译成Pattern对象
            Pattern p = Pattern.compile("^" + pattern);
            // 利用Pattern对象的matcher方法得到一个Matcher对象，用来匹配字符串
            Matcher m = p.matcher(url);
            // 如果匹配到了，返回true
            if (m.find()) {
                return true;
            }
        }
        return false;
    }
```

额外提一句现在最新的Spring Security（此处是6.0.2）的配置方式和以前不一样了，以前是用一个配置类，继承自WebSecurityConfigurerAdapter，然后链式配置：

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authrizeRequests()
                 .anyRequest().authenticated()
                 .and()
                 .xxxx();
    }
}
```

我们继承WebSecurityConfigurerAdapter来实现自定义类，主要是为了配置**HttpSecurity**和**WebSecurity**，前者主要是配置 Spring Security 中的过滤器链，后者则主要是配置一些路径放行规则。

而在6.x版本的Spring Security中，弃用了WebSecurityConfigurerAdapter，以后如果想要配置过滤器链，可以通过自定义 **SecurityFilterChain** Bean 来实现。以后如果想要配置 WebSecurity，可以通过 **WebSecurityCustomizer** Bean 来实现。并且从链式配置改成用**lambda表达式**。

比如：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.authorzeHttpRequest(auth->{
        auth.anyRequest().authenticated();
        })
        .formLogin(...)
        ...
        .build();
}
```

### Sentinel

#### 1. 简单介绍

Sentinel 是面向分布式、多语言异构化服务架构的流量治理组件，主要以流量为切入点，从流量路由、流量控制、流量整形、熔断降级、系统自适应过载保护、热点流量防护等多个维度来帮助开发者保障微服务的稳定性。

Sentinel 的使用可以分为两个部分:

- 核心库（Java 客户端）：不依赖任何框架/库，能够运行于 Java 8 及以上的版本的运行时环境，同时对 Dubbo / Spring Cloud 等框架也有较好的支持。
- 控制台（Dashboard）：Dashboard 主要负责管理推送规则、监控、管理机器信息等。

我们这里只用到Sentinel的限流功能，Dashboard也无需下载。

那么要实现限流，正常情况下，我们该采取什么样的策略呢？

- 方案一：**快速拒绝**，既然不再接受新的请求，那么我们可以直接返回一个拒绝信息，告诉用户访问频率过高。
- 方案二：**预热**，依然基于方案一，但是由于某些情况下高并发请求是在某一时刻突然到来，我们可以缓慢地将阈值提高到指定阈值，形成一个缓冲保护。
- 方案三：**排队等待**，不接受新的请求，但是也不直接拒绝，而是进队列先等一下，如果规定时间内能够执行，那么就执行，要是超时就算了。

针对于是否超过流量阈值的判断，这里我们提4种算法：

**漏桶算法**

顾名思义，就像一个桶开了一个小孔，水流进桶中的速度肯定是远大于水流出桶的速度的，这也是最简单的一种限流思路：

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918110550.png)

我们知道，桶是有容量的，所以当桶的容量已满时，就装不下水了，这时就只有丢弃请求了。利用这种思想，我们就可以写出一个简单的限流算法。

**令牌桶算法**

只能说有点像信号量机制。现在有一个令牌桶，这个桶是专门存放令牌的，每隔一段时间就向桶中丢入一个令牌（速度由我们指定）当新的请求到达时，将从桶中删除令牌，接着请求就可以通过并给到服务，但是如果桶中的令牌数量不足，那么不会删除令牌，而是让此数据包等待。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918110621.png)

可以试想一下，当流量下降时，令牌桶中的令牌会逐渐积累，这样如果突然出现高并发，那么就能在短时间内拿到大量的令牌。

**固定时间窗口算法**

我们可以对某一个时间段内的请求进行统计和计数，比如在`14:15`到`14:16`这一分钟内，请求量不能超过`100`，也就是一分钟之内不能超过`100`次请求，那么就可以像下面这样进行划分：

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918110643.png)

虽然这种模式看似比较合理，但是试想一下这种情况：

- 14 : 15 : 59的时候来了100个请求
- 14 : 16 : 01的时候又来了100个请求

出现上面这种情况，符合固定时间窗口算法的规则，所以这200个请求都能正常接受，但是，如果你反应比较快，应该发现了，我们其实希望的是60秒内只有100个请求，但是这种情况却是在3秒内出现了200个请求，很明显已经违背了我们的初衷。

因此，当遇到临界点时，固定时间窗口算法存在安全隐患。

**滑动时间窗口算法**

相对于固定窗口算法，滑动时间窗口算法更加灵活，它会动态移动窗口，重新进行计算：

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918110755.png)

虽然这样能够避免固定时间窗口的临界问题，但是这样显然是比固定窗口更加耗时的。

#### 2. 实际应用

我们需要设置两种规则，一种是流量控制规则，一种是热点参数规则。前者限制所有请求，后者限制某一IP的请求。重写拦截器的preHandle，在在请求处理之前进行调用（Controller方法调用之前）。

```java
    static {
        // 接口限流规则：所有的请求，限制每秒最多只能通过2000个，超出限制匀速排队，利用sentinel实现
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource(NOVEL_RESOURCE);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 限制每秒最多只能通过2000个请求(QPS=2000)
        rule1.setCount(2000);
        rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        rules.add(rule1);
        FlowRuleManager.loadRules(rules);

        // 接口防刷规则1：所有的请求，限制每个IP每秒最多只能通过50个，超出直接拒绝
        ParamFlowRule rule2 = new ParamFlowRule(NOVEL_RESOURCE)
                // 针对请求参数的第一个参数来限流，即NOVEL_RESOURCE
                .setParamIdx(0)
                .setCount(50);
        // 接口防刷规则2：所有的请求，限制每个IP每分钟最多只能通过1000个，超出直接拒绝
        ParamFlowRule rule3 = new ParamFlowRule(NOVEL_RESOURCE)
                .setParamIdx(0)
                .setCount(1000)
                .setDurationInSec(60);
        ParamFlowRuleManager.loadRules(Arrays.asList(rule2, rule3));
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = IpUtils.getRealIp(request);
        Entry entry = null;
        try {
            // 使用SphU.entry()函数获取名为NOVEL_RESOURCE的资源的entry
            // resource：资源名称
            // EntryType：流量类型，in代表入站流量，out代表出站流量
            // batchCount：count表示申请占用共享资源的数量，只有申请到足够的共享资源才能继续执行。
            // count的值一般为1，当限流规则配置的限流阈值类型为threads时，表示需要申请一个线程，当限流规则配置的限流阈值类型为qps时，表示需要申请1令牌（如果使用令牌桶算法）
            // ip：作为热点参数传入，请求访问资源的客户端的IP地址
            entry = SphU.entry(NOVEL_RESOURCE, EntryType.IN, 1, ip);
            // 调用父类的preHandle
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } catch (BlockException ex) {
            // 处理限流请求
            log.info("IP:{}被限流了！", ip);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(RestResp.fail(ErrorCodeEnum.USER_REQ_MANY)));
        } finally {
            if (entry != null) {
                // 若entry的时候传入了热点参数，那么exit的时候也一定要带上对应的参数（exit(count, args)），否则可能会有统计错误
                entry.exit(1, ip);
            }
        }
        return false;
    }
```

想要测试可以使用Postman的Runner来验证限流策略是否正常运行。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230918135549.png)

最后在config处设置拦截器生效顺序：

```java
    public void addInterceptors(InterceptorRegistry registry) {
        // 流量限制拦截器
        registry.addInterceptor(flowLimitInterceptor)
                .addPathPatterns("/**")
                .order(0);

        // 文件访问拦截器
        registry.addInterceptor(fileInterceptor)
                .addPathPatterns(SystemConfigConsts.IMAGE_UPLOAD_DIRECTORY + "**")
                .order(1);

        // 权限认证拦截器
        registry.addInterceptor(authInterceptor)
                // 拦截会员中心相关请求接口
                .addPathPatterns(ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/**",
                        // 拦截作家后台相关请求接口
                        ApiRouterConsts.API_AUTHOR_URL_PREFIX + "/**",
                        // 拦截平台后台相关请求接口
                        ApiRouterConsts.API_ADMIN_URL_PREFIX + "/**")
                // 放行登录注册相关请求接口
                .excludePathPatterns(ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/register",
                        ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/login",
                        ApiRouterConsts.API_ADMIN_URL_PREFIX + "/login")
                .order(2);

        // Token解析拦截器
        registry.addInterceptor(tokenParseInterceptor)
                // 拦截小说内容查询接口，需要解析 token 以判断该用户是否有权阅读该章节（付费章节是否已购买）
                .addPathPatterns(ApiRouterConsts.API_FRONT_BOOK_URL_PREFIX + "/content/*")
                .order(3);
    }
```

## 六、项目部署

> 更多问题解答查看原项目的教程文档[小说精品屋](https://docs.xxyopen.com/course/novel)

#### 后端

可以使用maven将项目打包成jar放到云服务器上运行，或者编写Dockerfile制作成镜像放到云服务器上的docker，在docker中运行。有些中间件不想在win10上运行，可以放到云服务器上运行，并且不同云服务器可以运行不同中间件，像elasticsearch这种比较吃性能的，可以一台云服务器只用一台，也可以放到win10中运行。

#### 前端

在[novel 前端项目](https://github.com/201206030/novel-front-web)下载前端项目源码，注意node的版本要求，本人配置的是14.17.0，建议使用nvm管理node版本。在项目根目录下运行 `yarn build` 命令构建，如果要真实运行，记得上传`dist`文件夹中的内容到到服务器`/root/nginx/html`目录中。

#### 问题

建议先把业务写好，边写边测试，别写完了再测试，有时候根本不报错，从数据库中读取数据之后卡在那里也有可能的，等到写完发现问题就很难去解决了。此外之前写的项目用的springfox，也就是比较多人用的swagger2.9.2，但是在springboot3.0中由于版本问题不适用（javax变成jakarta），因此要使用springdoc。springdoc配置更简单了，而且实际上实现也是swagger，本项目的swagger文档位置是：[Swagger UI](http://localhost:8888/swagger-ui/index.html)。

**跨域问题**

访问不同源（协议 + 域名 + 端口，这三者都一致才是同源）的资源，浏览器会进行限制，用于阻隔恶意文档，减少可能被攻击的媒介。跨域主要涉及4个响应头：

Access-Control-Allow-Origin 用于设置允许跨域请求源地址 （预检请求和正式请求在跨域时候都会验证）

Access-Control-Allow-Headers 跨域允许携带的特殊头信息字段 （只在预检请求验证）

Access-Control-Allow-Methods 跨域允许的请求方法或者说HTTP动词 （只在预检请求验证）

Access-Control-Allow-Credentials 是否允许跨域使用cookies，如果要跨域使用cookies，可以添加上此请求响应头，值设为true（设置或者不设置，都不会影响请求发送，只会影响在跨域时候是否要携带cookies，但是如果设置，预检请求和正式请求都需要设置）。不过不建议跨域使用（项目中用到过，不过不稳定，有些浏览器带不过去），除非必要，因为有很多方案可以代替>

一般后端的解决方法就是在spring security中下手，或者是使用nginx进行代理。

> 定制一个CorsFilter

```java
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许跨域的域，不能写*，不然不能携带cookie
        for (String allowOrigin : corsProperties.getAllowOrigins()) {
            config.addAllowedOrigin(allowOrigin);
        }
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的请求方式
        config.addAllowedMethod("*");
        // 是否允许携带cookie信息
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        // 添加映射路径，拦截一切请求
        configurationSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configurationSource);
    }
```

> nginx

本质上和上面区别不大，都是得益于cors的规范化方案。

```java
http {
  # 其他配置...

  # 添加跨域配置
  server {
    listen 80;
    server_name example.com;

    location / {
      add_header 'Access-Control-Allow-Origin' '*';
      add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS';
      add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range';
      add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range';
    }
  }
}
```

**rabbitmq异常**

```yaml
  rabbitmq:
    addresses: localhost
    username: admin
    password: admin
    virtual-host: /
```

这时候前端传入的数据后端没有校验，比如email不去检验就传到队列中，rabbitmq会一直抛出异常。这是因为信息存在错误，无法正常消费，触发了rabbitmq的重试机制，重试机制是默认开启的，但是如果没有重试机制相关的配置会导致消息一直无间隔的重试，直到消费成功。当传入的数据是存在错误的，无法正常消费时就会陷入死循环。

解决问题的思路就是，首先后端要做数据校验，确认符合格式再传入队列，然后rabbitmq设置max-attempts（最大重试次数）和initial-interval（重试间隔(ms)），并且设置死信队列和死信交换机，把成为死信（消息被拒/消息 TTL 过期/队列满了，无法再添加）的消息发送到死信队列中（用在较为重要的业务队列中，确保未被正确消费的消息不被丢弃，待后续排查时处理）。当时我的做法就是，将原队列删掉，以此清除错误传入的信息，然后开启数据校验，重传机制。
