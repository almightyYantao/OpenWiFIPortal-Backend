# Wi-Fi 的 Portal 认证后端

## 环境准备

- JAVA18
- Mysql

## 支持验证方式

- [x] 短信认证
- [x] 企业微信代扫码
- [ ] LDAP 认证
- [ ] 飞书认证


## 配置修改

修改`application-prod.properties`中的数据库参数和`Radius`参数
企业微信的默认配置在`application.properties`中，当然如果你不想要用的话，可以不开

## 部署方式

### 编译

```shell
maven clean package
```
### 部署

尽量把配置文件放到外面来吧，要不然的话，每次修改都要打包，很麻烦
```shell
java -jar xxx.jar --spring.config.location=xxxxxx
```
