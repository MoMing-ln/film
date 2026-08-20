# 影院在线售票与管理系统

这是一个以影院业务为背景的传统 Java Web 毕业设计项目，主要覆盖电影信息展示、影片预告、排片管理、选座购票、订单处理、退票、评论、用户资料和后台用户管理等功能。

这个项目大约完成于 2017 年，是一次完整的课程/毕业设计实践。它采用当时比较常见的 Spring MVC + Spring + MyBatis + Velocity 技术组合，以 WAR 包的形式部署到 Servlet 容器中。项目现在更适合作为一份历史代码、毕业设计存档和传统 Java Web 学习材料，而不是直接用于生产环境。

## 项目解决什么问题

项目模拟一个小型影院的线上售票系统：

1. 用户可以浏览影院中的影片、查看影片详情和预告片。
2. 用户可以按照影片、日期和时间查看场次。
3. 用户可以选择影厅和座位，创建订单并完成一个模拟支付流程。
4. 用户可以查看订单、取得票码、退票，并在退票后收到邮件通知。
5. 用户可以发表评论、修改个人资料、上传头像和修改密码。
6. 管理员可以维护影片、影片分类、地区、影厅、排片和用户角色。

简化后的业务链路如下：

```text
浏览影片
   ↓
查看影片详情 / 预告片 / 评论
   ↓
选择场次
   ↓
选择座位
   ↓
创建待支付订单
   ↓
模拟支付，生成票码并锁定座位
   ↓
查看订单 / 退票
```

## 功能概览

### 面向普通用户

- 首页影片列表
- 影片详情和预告片预览
- 按影片、日期、时间查询排片
- 注册、登录、退出登录
- 支持使用用户名、邮箱或手机号登录
- 记住登录状态的 Cookie 登录
- 查看个人资料
- 修改个人资料和头像
- 修改密码
- 忘记密码并通过邮件获得随机新密码
- 发表评论
- 按场次选择座位
- 创建订单
- 查看订单详情和已选座位
- 模拟支付并获得票码
- 退票
- 退票后发送邮件通知

### 面向影院后台

- 影片列表管理
- 添加影片
- 修改影片
- 删除影片
- 上传影片海报和预告视频
- 影厅和座位数据读取
- 排片列表管理
- 新增、修改和删除排片
- 用户列表管理
- 新增用户
- 删除用户
- 冻结/解冻用户（代码中通过用户数据更新实现）
- 修改用户资料和角色
- 查看角色列表

### 权限模型

项目使用自定义的 `@Login` 注解和 `AuthenticationInterceptor` 拦截器进行登录与权限控制。角色权限不是基于现代意义上的细粒度权限点，而是使用一个整数级别进行比较：当前用户的 `access_level` 大于等于页面要求的级别时，才允许访问。

| 角色 | `access_level` | 说明 |
| --- | ---: | --- |
| `ROLE_ANONYMOUS` | 0 | 游客 |
| `ROLE_USER` | 1 | 普通用户 |
| `ROLE_ADMIN_GENERAL` | 4096 | 系统管理员 |
| `ROLE_DEVELOPER` | 16384 | 开发人员 |
| `ROLE_SUPERVISOR` | 32768 | 超级管理员 |

影片管理、排片管理和用户管理控制器要求至少达到 `ADMIN_GENERAL` 级别；订单、评论、个人资料和密码相关功能要求登录。首页、影片详情、注册和登录页面可以匿名访问。

## 技术栈

项目使用的是 2017 年前后的技术版本，版本信息主要来自 `pom.xml`、Spring 配置文件和数据库导出文件。

| 层次 | 技术 |
| --- | --- |
| 构建 | Maven |
| 发布形态 | WAR |
| 编程语言 | Java 7 语法级别 |
| Web 框架 | Spring MVC 3.2.2.RELEASE |
| IoC/事务基础 | Spring 3.2.2.RELEASE |
| 持久层 | MyBatis 3.2.2 + MyBatis-Spring 1.2.0 |
| 模板引擎 | Apache Velocity 1.6 + Velocity Tools 2.0 |
| 数据库 | MySQL，历史导出文件来自 MySQL 5.5.28 |
| 数据库连接池 | Apache Commons DBCP 2.1 |
| JDBC 驱动 | MySQL Connector/J 5.1.37 |
| 前端 | jQuery 1.11.2、Bootstrap、jQuery UI、传统 JavaScript |
| Servlet API | Servlet 2.5 |
| 文件上传 | Commons FileUpload |
| 邮件 | JavaMail |
| 缓存 | XMemcached / Memcached |
| 日志 | SLF4J + Log4j 1.2 |
| 定时任务依赖 | Quartz 2.2.1 |
| 开发容器插件 | Jetty Maven Plugin 9.2.3.v20140905 |

## 系统架构

项目采用典型的分层结构：

```text
浏览器
  ↓ HTTP / 表单 / Ajax
Spring MVC DispatcherServlet
  ↓
AuthenticationInterceptor
  ↓
Controller
  ↓
Service / ServiceImpl
  ↓
MyBatis Mapper 接口 + Mapper XML
  ↓
MySQL
```

页面渲染使用 Velocity 模板，模板文件位于 `src/main/webapp/WEB-INF/velocity`。Spring MVC 负责将 Controller 返回的视图名解析为 `.vm` 文件。

登录状态主要保存在 HTTP Session 中，同时使用 Cookie 保存经过 DES 处理的用户标识，用于尝试恢复登录状态。权限拦截器会在请求进入 Controller 之前检查 Session、Cookie、用户角色和 `@Login(level = ...)` 要求。

## 代码目录

```text
.
├── film.sql                              # MySQL 表结构和演示数据
├── pom.xml                               # Maven 项目及依赖配置
└── src/main
    ├── java/com/ln/film
    │   ├── controller/                   # Spring MVC 控制器
    │   ├── service/                      # 业务接口和实现
    │   ├── dao/                          # MyBatis Mapper 接口
    │   ├── model/                        # 数据模型和 MyBatis Generator 类
    │   │   ├── vo/                       # 页面组合对象
    │   │   └── transients/               # 权限等级等非持久化对象
    │   ├── form/                         # 表单对象
    │   ├── validator/                    # 表单校验
    │   ├── interceptor/                  # 登录和权限拦截器
    │   ├── annotation/                   # 自定义注解，例如 @Login
    │   ├── exception/                    # 业务异常
    │   └── utils/                        # Cookie、邮件、加密等工具
    ├── resources
    │   ├── applicationContext.xml         # Spring 根容器配置
    │   ├── film-mvc.xml                  # Spring MVC 配置
    │   ├── config.properties             # 数据库、邮件、Memcached 配置
    │   ├── mapper/                       # MyBatis SQL 映射文件
    │   ├── i18n/                         # 国际化消息
    │   └── log4j.xml                     # 日志配置
    └── webapp
        ├── index.html                    # Web 应用入口
        ├── WEB-INF/velocity/             # Velocity 页面模板
        └── assert/                       # CSS、JavaScript、图片和字体
```

## 数据模型

数据库脚本是 `film.sql`。核心表及其职责如下：

| 表 | 作用 |
| --- | --- |
| `users` | 用户基本信息、登录凭据、角色和头像 |
| `role` | 角色名称、显示名称和权限等级 |
| `film` | 影片名称、导演、演员、简介、语言、时长、类型、海报、视频和价格 |
| `sort` | 影片类型，例如动作片、剧情片、科幻片 |
| `area` | 影片地区，例如大陆、港台、欧美、日韩 |
| `hall` | 影厅名称和座位容量 |
| `seat` | 影厅中的座位行列信息 |
| `timetable` | 影片、影厅、放映日期和放映时间的关联 |
| `orders` | 用户订单、场次、金额、座位数量和票码 |
| `order_seat` | 订单与座位的关联及座位状态 |
| `filmcomment` | 用户对影片的评论 |

订单和座位状态在代码中大致按照下面的方式流转：

```text
选择座位
  ↓
创建订单，票码为“未支付”
  ↓
写入 order_seat，状态为 1
  ↓
模拟支付
  ↓
生成随机票码，order_seat 状态改为 0
  ↓
退票
  ↓
票码改为“已退票”，order_seat 状态恢复为 1
```

这里的状态值是历史实现中的约定，不建议在新系统中直接照搬。项目没有真正接入第三方支付、分布式锁或数据库级别的座位并发控制。

## 运行前提

理论上可以按照当年的方式运行：

- JDK 7 或一个能够兼容 Java 7 字节码和旧版依赖的 JDK
- Maven
- MySQL 5.x 或经过兼容性调整的 MySQL 环境
- 可选：Memcached，默认地址为 `127.0.0.1:11211`
- 可选：SMTP 邮箱，用于找回密码和退票邮件
- 可选：Jetty 或其他支持 Servlet 2.5 的 Servlet 容器

由于 Spring 3、Servlet 2.5、Velocity 1.6、Log4j 1.x、MySQL Connector/J 5.1 等依赖都已经比较老，项目没有承诺可以在 2026 年的现代 JDK 和容器中直接启动。现代环境可能遇到 Java 模块限制、旧 API 移除、依赖解析失败、模板引擎兼容性或 MySQL 驱动连接参数问题。

## 配置说明

主要配置文件是：

```text
src/main/resources/config.properties
```

需要至少配置数据库连接：

```properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/film?useUnicode=true&characterEncoding=UTF-8
username=your_db_user
password=your_db_password
```

如需启用邮件功能，再配置 SMTP：

```properties
smtp.host=smtp.example.com
smtp.user=your_mail@example.com
smtp.password=your_mail_password_or_app_password
```

Memcached 配置如下，当前代码中主要由 `CacheService` 提供缓存访问能力，密码重置流程中的缓存实现曾经保留为注释代码：

```properties
memcache.prefix=film_
memcache.host=127.0.0.1:11211
memcache.expire=3600
```

数据库生成器配置还位于：

```text
src/main/resources/generatorConfig.xml
```

如果只是运行现有项目，不需要执行代码生成器；只有在重新生成 MyBatis Model、Example、Mapper 类时才需要修改其中的数据库连接信息。

## 初始化数据库

创建数据库并导入脚本：

```sql
CREATE DATABASE film DEFAULT CHARACTER SET utf8;
```

然后导入：

```bash
mysql -u your_db_user -p film < film.sql
```

脚本中包含 2017 年的影片、排片、订单、角色和用户演示数据。它是为了展示页面效果和业务流程而保留的历史数据，不代表当前影院信息。

## 构建和启动

安装依赖并打包 WAR：

```bash
mvn clean package -DskipTests
```

项目配置了 Jetty Maven Plugin，按原始设计可以尝试：

```bash
mvn jetty:run
```

启动后通常通过以下地址访问：

```text
http://localhost:8080/
```

如果使用外部 Tomcat/Jetty，则将 `target/film-0.0.1-SNAPSHOT.war` 部署到容器中。具体 WAR 文件名以 Maven 构建结果为准。

## 主要页面和接口入口

以下是代码中比较重要的 URL，便于快速理解系统入口：

| URL | 作用 | 权限 |
| --- | --- | --- |
| `/` 或 `/index` | 首页影片列表 | 游客可访问 |
| `/preview/showfilm?fid=...` | 影片详情、预告片和评论 | 游客可访问 |
| `/query` | 查看排片查询页面 | 游客可访问页面，查询 Ajax 需要登录 |
| `/register` | 注册 | 游客 |
| `/login` | 登录 | 游客 |
| `/logout` | 退出登录 | 已登录 |
| `/order/buy/{fid}` | 根据影片选择场次 | 普通用户 |
| `/order/selectSeat` | 选择座位 | 普通用户 |
| `/order/pay` | 创建待支付订单并进入支付页面 | 普通用户 |
| `/order/pay/{oid}` | 模拟支付并生成票码 | 普通用户 |
| `/order/list` | 我的订单 | 普通用户 |
| `/order/return/{oid}` | 退票 | 普通用户 |
| `/profile`、`/settings` | 个人资料 | 普通用户 |
| `/password/update` | 修改密码 | 普通用户 |
| `/password/forget` | 忘记密码 | 游客/用户入口 |
| `/film` | 影片后台管理 | 管理员及以上 |
| `/timetable/list` | 排片后台管理 | 管理员及以上 |
| `/usermanage/showmanage` | 用户后台管理 | 管理员及以上 |

部分页面使用表单提交，部分操作使用 Ajax 和 JSON 请求；返回结构主要使用 `AjaxResult` 封装。

## 邮件、上传和缓存

### 邮件

`EmailUtils` 使用 JavaMail 连接 SMTP 服务，当前业务中有两类邮件：

- 忘记密码：生成一个随机密码，发送给用户，发送成功后才更新数据库中的密码。
- 退票通知：退票成功后向用户邮箱发送通知。

### 文件上传

影片后台会上传：

- 影片海报：保存到 Web 应用运行目录下的 `/upload/images/`
- 影片预告片：保存到 Web 应用运行目录下的 `/upload/video/`

用户头像会保存到 `/upload/avatar-dir/`。文件名使用 UUID 加原始扩展名生成，数据库只保存访问路径。

这是一种依赖 Servlet 容器展开目录的旧式存储方式。应用重新部署、容器清理临时目录或使用多实例部署时，上传文件可能丢失或无法共享。

### Memcached

项目提供了 `CacheService`，封装了 XMemcached 的设置、读取和删除操作，并支持统一前缀和过期时间。它反映了当时对缓存的设计尝试，但并不是所有业务查询都已经接入缓存。

## 安全和生产环境注意事项

这个仓库是历史毕业设计，不应直接作为生产系统部署。至少需要注意以下问题：

- 配置文件中的数据库、SMTP 和 Memcached 参数必须替换为自己的值，不能使用公开仓库中的占位配置。
- 用户密码使用无盐 MD5 保存，不符合现代密码存储要求，应迁移到 Argon2、bcrypt 或 scrypt。
- Cookie 登录使用固定 DES 密钥，且算法和密钥管理方式已经过时。
- 代码中存在将密码写入日志的历史调试语句，公开部署前应删除敏感日志。
- 模拟支付接口会直接把支付视为成功，没有真实支付渠道、签名校验、幂等控制或支付回调验证。
- 座位占用流程缺少可靠的事务、唯一约束和并发控制，多个用户同时购买同一座位时可能产生超卖。
- 部分删除、退票等操作通过 GET 请求触发，不符合现代 Web 安全实践。
- 上传文件缺少完善的类型、大小、内容和访问权限校验。
- 页面和接口缺少现代 CSRF 防护、速率限制和更细粒度的权限校验。
- 使用 Log4j 1.x、Servlet 2.5 和大量旧版前端依赖，建议仅在隔离的学习环境中运行。
- `film.sql` 包含演示用户、邮箱、手机号、票码和旧密码摘要等历史样例数据，不能将其视为真实账号，也不要复用其中的密码。

## 已知限制

目前项目更适合“阅读和复现”，不适合直接升级后上线。已知限制包括：

1. 没有测试目录和自动化测试覆盖，关键业务主要依靠页面操作验证。
2. 支付只是本地模拟，票码由随机数拼接生成。
3. 订单状态并没有独立、清晰的枚举模型，部分状态信息通过票码字段中的中文文本表示。
4. 座位表的初始化数据非常少，数据库脚本中的演示影厅并没有完整生成大规模座位。
5. 上传文件存储在应用目录，不适合容器化、多实例和独立静态资源服务。
6. 部分配置和类名仍然带有早期项目的历史命名，例如 `tps`、`filmservice` 等。
7. 项目依赖的 Java、Servlet、Spring、Velocity 和 MySQL 驱动版本较老，现代环境运行可能需要额外兼容处理。
8. 代码中存在一些注释掉的历史实现和未完成的业务分支，它们保留了项目演进过程，但不代表当前功能可用。

## 这个项目值得保留的地方

虽然技术栈已经被淘汰，但它完整记录了一个从需求、数据库、页面、权限到业务流程的 Web 项目实践：

- 有清晰的 Controller、Service、DAO 分层。
- 有独立的表单对象、校验器、异常类型和 View Object。
- 有影片、排片、影厅、座位、订单、用户、角色等较完整的业务模型。
- 有从浏览影片到购票、支付、退票的连续业务链路。
- 有后台管理和基于角色级别的权限控制。
- 有邮件、缓存、文件上传、Cookie 登录等当时常见的工程化尝试。

因此，这个项目更像是一份“某个阶段的开发者如何构建一个完整业务系统”的记录，而不仅仅是一组已经过时的代码。

## 历史信息

- 数据库导出文件标记的时间：2017 年 4 月 2 日
- Git 历史中主要开发提交集中在：2017 年 3 月至 4 月
- 项目最后还有一次 2020 年的提交记录
- 原始项目类型：Java Web / Maven Web Application / WAR

## 许可证

当前仓库没有附带明确的开源许可证。如果准备正式公开或允许他人复用，建议补充一个许可证文件，例如 MIT、Apache-2.0，或者明确声明仅供学习和归档使用。

