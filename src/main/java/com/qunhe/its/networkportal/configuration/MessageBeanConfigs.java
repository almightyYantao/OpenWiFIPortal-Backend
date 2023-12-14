package com.qunhe.its.networkportal.configuration;


import com.qunhe.its.networkportal.common.wxwork.WxWorkMessage;
import com.qunhe.its.networkportal.user.utils.PropertiesUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBeanConfigs {

    @Bean
    public WxWorkMessage wxWorkUtils() {
        return new WxWorkMessage(
                PropertiesUtils.properties.getProperty("wxwork.agentId"),
                PropertiesUtils.properties.getProperty("wxwork.secret"),
                PropertiesUtils.properties.getProperty("wxwork.corpId")
        );
    }

}