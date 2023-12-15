# Wi-Fi 的 Portal 认证后端

## 环境准备

- JAVA18
- Mysql

## 后期计划
- [ ] 后台管理
- [ ] 计费管理
- [x] 强制下线
- [ ] SSID 管理认证
- [ ] Radius 客户端配置

## 支持验证方式

- [x] 短信认证
- [x] 企业微信代扫码
- [ ] LDAP 认证
- [ ] 飞书认证


## 配置修改

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

## 日志文件配置
logging.config=classpath:log4j2.xml
log.file.path=logs

## SSE 超时时间
spring.mvc.async.request-timeout=-1
```

## 部署方式

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
```shell
#!/bin/bash

# 设置下载文件的URL
JAR_URL='https://it-service-cos.kujiale.com/xiaoku/portal.jar'
ZIP_URL='https://it-service-cos.kujiale.com/xiaoku/portal-frontend.zip'

# 设置目标目录
BASE_DIR="/portal"
FRONTEND_DIR=$BASE_DIR/frontend

# 创建目标目录
mkdir -p $BASE_DIR $FRONTEND_DIR

# 下载文件
curl -o $BASE_DIR/portal.jar $JAR_URL
curl -o $BASE_DIR/portal-frontend.zip $ZIP_URL

# 检查 unzip 是否安装
if ! command -v unzip &> /dev/null; then
    echo 'Error: unzip command not found. Attempting to install...'
    # 尝试安装 unzip
    if command -v apt-get &> /dev/null; then
        sudo apt-get update
        sudo apt-get install -y unzip
    elif command -v yum &> /dev/null; then
        sudo yum install -y unzip
    else
        echo 'Error: Unable to install unzip. Please install it manually and run the script again.'
        exit 1
    fi
fi

# 解压前端文件到指定目录
unzip -q $BASE_DIR/portal-frontend.zip -d $FRONTEND_DIR

# 清理下载的 zip 文件
rm $BASE_DIR/portal-frontend.zip

echo '==========依赖包下载完成=========='

# Portal service script
SERVICE_SCRIPT="#!/bin/bash

SERVICE_NAME='portal-service'
JAR_PATH='/portal/portal.jar'
CONFIG_LOCATION='--spring.config.location=/portal/application.properties'
LOG_PATH='/portal/logs'
PID_FILE='/var/run/\${SERVICE_NAME}.pid'

start() {
    echo 'Starting \$SERVICE_NAME...'
    if [ -f '\$PID_FILE' ] && kill -0 \"\$(cat '\$PID_FILE')\"; then
        echo '\$SERVICE_NAME is already running.'
        exit 1
    fi
    nohup /usr/bin/java -jar \$JAR_PATH \$CONFIG_LOCATION > \$LOG_PATH/service.log 2>&1 &
    echo \$! > '\$PID_FILE'
    echo '\$SERVICE_NAME started successfully.'
}

stop() {
    echo 'Stopping \$SERVICE_NAME...'
    if [ ! -f '\$PID_FILE' ] || ! kill -0 \"\$(cat '\$PID_FILE')\"; then
        echo '\$SERVICE_NAME is not running.'
        exit 1
    fi
    kill -15 \"\$(cat '\$PID_FILE')\" && rm -f '\$PID_FILE'
    echo '\$SERVICE_NAME stopped successfully.'
}

restart() {
    stop
    sleep 2
    start
}

case \"\$1\" in
    start)
        start
    ;;

    stop)
        stop
    ;;

    restart)
        restart
    ;;

    *)
        echo 'Usage: \$0 {start|stop|restart}'
        exit 1
    ;;
esac

exit 0
"

# Save scripts to files
echo "$SERVICE_SCRIPT" > /portal/portal-service.sh

# Set execute permissions
chmod +x /portal/portal-service.sh

IP_ADDRESS=$(hostname -I | cut -d' ' -f1)

echo '==========程序安装完成=========='
echo "=== Radius 地址：$IP_ADDRESS:1812"
echo "=== Radius 计费地址：$IP_ADDRESS:1813"
echo "=== 认证地址：$IP_ADDRESS"
```