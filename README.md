# Wi-Fi 的 Portal 认证后端

这是一个开源的 Portal 认证协议，主要用于 AC 的 Wi-Fi 认证，目前只支持 Portal 协议，其他的还不支持，后续会增加更多的协议支持。  
欢迎大家提交 ISSUES~

## 🌸 环境准备

- JAVA18
- Mysql

## 😆 后期计划
- [ ] 后台管理
- [ ] 计费管理
- [x] 强制下线
- [ ] SSID 管理认证
- [ ] Radius 客户端配置
- [ ] 黑名单

## ✨ 支持
### 支持验证方式

- [x] 短信认证 
  - [x] URL 自定义短信接口
  - [ ] 其他短信接口
- [x] 企业微信代扫码
- [ ] 自定义账号密码登录
- [ ] LDAP 认证
- [ ] 飞书认证

### 支持协议

- [x] Portal认证

### 支持设备

- [x] 华为 AC
- [ ] 其他等


## 🏳️ 配置修改

```properties
## 企业微信配置
wxwork.agentId=xxxx
wxwork.secret=xxxx
wxwork.corpId=xxxx

## 数据库配置
spring.datasource.master.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.master.jdbc-url=jdbc:mysql://xxxxx/it_portal
spring.datasource.master.username=xxxx
spring.datasource.master.password=xxxx
mybatis.configuration.map-underscore-to-camel-case=true

## Radius 配置
radius.secret=xxxx

## NAS 配置
nas.ip=xxxxxx
nas.port=2000

## SMS 配置
sms.type=url
sms.url=xxxxx

## 日志文件配置
logging.config=classpath:log4j2.xml
log.file.path=logs

## SSE 超时时间
spring.mvc.async.request-timeout=-1
```

## 🌺 部署方式

### 编译

```shell
mvn clean package -Dmaven.test.skip=true
```
### 部署

尽量把配置文件放到外面来吧，要不然的话，每次修改都要打包，很麻烦
```shell
java -jar xxx.jar --spring.config.location=xxxxxx
```
### 导入 SQL

将 resources 下的 `sql.sql` 文件导入到你的数据库中

## 快速安装脚本

不像编译，想直接使用的，那么直接可以运行 `install.sh` 脚本  
最好不要用`sh install.sh` 可能会出问题
```shell
chmod +x install.sh
./install
```

## 💜 Stat Time

<picture>
  <img
    alt="Star History Chart"
    src="https://api.star-history.com/svg?repos=almightyYantao/OpenWiFIPortal-Backend&type=Date"
  />
</picture>