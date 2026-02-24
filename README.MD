# Fantasy Music
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

一个基于 Spring Boot 开发的个人音乐平台系统，支持音乐的上传、下载、在线听歌、歌单管理等功能。

**项目前端页面项目地址: [FantasyMusic-web](https://github.com/ReReverie/FantasyMusic-web)**

## 简介

该项目具有用户注册、登录、权限管理等功能，提供了一个相对友好的用户界面，方便用户浏览和管理自己的音乐库。
项目主要用于学习和实践完整的后端开发流程，涵盖了数据库设计、RESTful API 开发、用户认证和授权等方面的内容。

## 已实现的功能 ##

- 用户注册 / 登录（JWT 认证）
- 用户权限（普通用户 / 管理员）
- 音乐上传（解析音频元数据）
- 在线音乐播放
- 音乐下载（权限控制）
- 歌单管理
- 用户信息管理
- Redis 缓存优化
- OSS 云存储（音乐文件与封面）

## 快速开始

### 环境要求

你需要准备并安装好下面的需求软件

- [JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [MySQL 8](https://dev.mysql.com/downloads/installer/)
- [Redis 8.4+](https://redis.io/downloads/)
- Gradle 9.3 仓库内包含Gradle Wrapper

### 前置步骤

请使用[OpenSSL](https://www.openssl-library.org/source/)或者ssh-keygen生成对应的密钥对

以下是OpenSSL步骤

```bash
# 生成 2048 位私钥（PEM）
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private_key.pem
```

### RSA 私钥配置

用于后端登录密码解密，需与前端保持一致。

```yaml
# application-local.yml 不能为多行字符串,需要将私钥内容转换为单行字符串
private-key-string: MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgE........必须为单行
```

### 1. 克隆项目

```
git clone https://github.com/ReReverie/FantasyMusic.git
cd FantasyMusic
```

### 2.创建数据库

项目提供了[数据库](sql/fantasy_music_database.sql)用于初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS fantasy_music 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

# 执行 SQL 脚本（退出 MySQL 后执行）
mysql -u root -p fantasy_music < sql/fantasy_music_database.sql
```

如果运行过程中没有出现错误提示,表示创建成功

### 3.修改配置文件

在项目目录下提供了一个[**application-local.yml**](./application-local.yml)的配置文件,请按照文件中提示进行修改

### 4.编译项目 & 运行项目

在项目根目录下执行

```bash
./gradlew bootRun
```

运行项目,项目会读取配置文件进行初始化,如果没有问题会输出类似提示,表示后端服务运行成功

```bash
INFO 42652 --- [fm-service] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
INFO 42652 --- [fm-service] [           main] c.f.fm.web.FantasyMusicApplication       : Started FantasyMusicApplication in 4.197 seconds (process running for 4.733)
```

如果你要编译单独运行的jar包,请使用下面的命令

```cmd
gradlew build
```

稍微等待一会后,jar会放在项目目录下的**build/libs/FantasyMusic-x.x.x-SNAPSHOT.jar**,使用这个jar包时,请将配置文件[**application-local.yml**](./application-local.yml)放在同级目录下.

### 5.访问接口文档

修改配置文件中下方位置为true后,重新启动项目

```yaml
springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
```

浏览器访问http://localhost:8080/swagger-ui/index.html即可浏览接口文档

## 项目结构

FantasyMusic
├─ fm-common    公共模块（工具类、配置相关、常量、异常、统一返回）
├─ fm-pojo      实体类 / DTO / VO / QUERY
├─ fm-service   业务逻辑层（Service + Mapper）
└─ fm-web       Web层（Controller、全局异常处理器切面类、配置类、启动类）

## 项目架构 ##

```
前端 (FantasyMusic-web)
        │
        ▼
fm-web (Controller 层)
        │
        ▼
fm-service (业务逻辑层)
        │
        ▼
fm-pojo / fm-common
        │
        ▼
MySQL + Redis + OSS
```

## 技术栈

后端：
- [Spring Boot](https://spring.io/projects/spring-boot) 核心开发框架，用于构建后端服务、提供 REST API，并管理整体应用配置和运行。
- [Spring Data Redis](https://spring.io/projects/spring-data-redis) 用于与 Redis 进行交互，实现用户信息、音乐列表等数据的缓存，提高系统访问速度。
- [MyBatis Plus](https://baomidou.com/) 简化数据库操作的 ORM 框架，减少大量重复的 CRUD 代码，提高开发效率。
- [Jaudiotagger](https://bitbucket.org/ijabz/jaudiotagger) 解析音乐文件的元数据，例如歌曲名、歌手、专辑、时长等信息。
- [JSON Web Token](https://jwt.io/) 实现用户登录认证和身份校验，保证接口访问的安全性。
- OSS（阿里云对象存储）用于存储音乐文件，实现文件的上传、下载与访问管理。

数据库：
- MySQL 用于存储系统核心业务数据，例如用户信息、音乐信息、歌单等。
- Redis 用作缓存数据库，减少对 MySQL 的直接访问。

构建工具：
- Gradle 用于项目依赖管理、构建和多模块管理。

其他：
- AOP 用于实现统一的权限校验等横切逻辑，减少重复代码。
- 全局异常处理 统一处理系统异常，返回规范化的接口响应结果。
- 多模块架构 项目分为多个模块,便于开发和可维护性。

## 项目截图

|             PC 端              |                        移动端                         |
| :----------------------------: | :---------------------------------------------------: |
| ![PC 截图](screenshots/pc.png) | <img src="screenshots/phone.png" style="zoom:50%;" /> |

## 未来计划

- 歌词解析

- 音乐分类

- 音乐推荐系统（大模型智能推荐、根据歌单推荐、基于用户行为推荐）
- 智能音乐搜索

## 许可证 ##

[MIT](LICENSE) © [ReReverie](https://github.com/ReReverie)

## 贡献说明 ##

欢迎提交 Issue 或 Pull Request 来改进本项目。