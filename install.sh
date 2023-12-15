#!/bin/bash

# 设置下载文件的URL
JAR_URL='https://it-service-cos.kujiale.com/xiaoku/portal.jar'
ZIP_URL='https://it-service-cos.kujiale.com/xiaoku/portal-frontend.zip'

# 设置目标目录
BASE_DIR="/portal"
FRONTEND_DIR=$BASE_DIR/frontend

# 创建目标目录
mkdir -p $BASE_DIR $FRONTEND_DIR

# 删除老文件
rm -rf $BASE_DIR/portal.jar
rm -rf $BASE_DIR/portal-frontend.zip

# 下载文件
curl -o $BASE_DIR/portal.jar $JAR_URL
curl -o $BASE_DIR/portal-frontend.zip $ZIP_URL


echo '==========检查依赖包=========='

# 检查 unzip 是否安装
if ! command -v unzip &> /dev/null; then
    echo "unzip 未安装，尝试安装..."

    # 使用 apt-get 安装 unzip，需要 sudo 权限
    if command -v apt-get &> /dev/null; then
        sudo apt-get update
        sudo apt-get install -y unzip
    else
        echo "无法确定系统类型，无法安装 unzip。请手动安装后重新运行脚本。"
        exit 1
    fi

    # 再次检查是否安装成功
    if ! command -v unzip &> /dev/null; then
        echo "unzip 安装失败，请手动安装后重新运行脚本。"
        exit 1
    fi
fi

echo '==========检查 nginx 部署=========='
# 检查 nginx 是否安装
if ! command -v nginx &> /dev/null; then
    echo "Nginx 未安装，尝试安装..."
    # 使用 apt-get 安装 nginx，需要 sudo 权限
    if command -v apt-get &> /dev/null; then
        sudo apt-get update
        sudo apt-get install -y nginx
    elif command -v yum &> /dev/null; then
        sudo yum install -y nginx
    else
        echo "无法确定系统类型，无法安装 Nginx。请手动安装后重新运行脚本。"
        exit 1
    fi
    # 再次检查是否安装成功
    if ! command -v nginx &> /dev/null; then
        echo "Nginx 安装失败，请手动安装后重新运行脚本。"
        exit 1
    fi
fi

NGINX_CONF_PATH="/etc/nginx/conf.d/portal.conf"

# 输出配置到文件
echo "server {
    listen 80;
    server_name it-portal.qunhequnhe.com;

    root /portal/frontend;
    index index.html index.htm;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    # 添加其他配置，如需要反向代理或者其他功能时
    # location /api/ {
    #     proxy_pass http://backend_server;
    #     ...
    # }

    # 错误页面配置
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /usr/share/nginx/html;
    }

    # 日志配置
    access_log /var/log/nginx/your_domain_access.log;
    error_log /var/log/nginx/your_domain_error.log;
}" | sudo tee "$NGINX_CONF_PATH"

# 重启 Nginx 以应用配置
sudo systemctl restart nginx

# 检查重启是否成功
if [ $? -ne 0 ]; then
    echo "Nginx 启动失败，脚本结束。"
    exit 1
fi

echo "Nginx 配置已写入 $NGINX_CONF_PATH，并已重启 Nginx。"


# 解压前端文件到指定目录
rm -rf $FRONTEND_DIR
unzip -q $BASE_DIR/portal-frontend.zip -d $FRONTEND_DIR

# 清理下载的 zip 文件
rm -rf $BASE_DIR/portal-frontend.zip

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
echo "## 企业微信配置
      wxwork.agentId=1000494
      wxwork.secret=zO_GIKzxHT9wHau7MlpoVMxSh_cimKpKHPQ4jPaPY5I
      wxwork.corpId=wxe838eaf3a3855809

      ## 数据库配置
      spring.datasource.master.driver-class-name=com.mysql.cj.jdbc.Driver
      spring.datasource.master.jdbc-url=jdbc:mysql://10.101.2.81:32610/it_portal
      spring.datasource.master.username=dbuser
      spring.datasource.master.password=EFyx5M3za2^W
      mybatis.configuration.map-underscore-to-camel-case=true

      ## Radius 配置
      radius.secret=qunhequnhe

      ## NAS 配置
      nas.ip=10.10.200.55
      nas.port=2000

      ## SMS 配置
      sms.type=url
      sms.url=xxxxxx

      ## 日志文件配置
      logging.config=classpath:log4j2.xml
      log.file.path=logs

      ## SSE 超时时间
      spring.mvc.async.request-timeout=-1" | sudo tee "$BASE_DIR"

# Set execute permissions
chmod +x /portal/portal-service.sh

IP_ADDRESS=$(hostname -I | cut -d' ' -f1)

echo '==========程序安装完成=========='
echo "=== Radius 地址：$IP_ADDRESS:1812"
echo "=== Radius 计费地址：$IP_ADDRESS:1813"
echo "=== 认证地址：http://$IP_ADDRESS"

#echo '==========启动程序=========='
#/portal/portal-service.sh start
#echo '==========启动完成=========='