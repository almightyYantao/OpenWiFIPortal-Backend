package com.qunhe.its.networkportal.configuration;


import com.qunhe.its.networkportal.common.wxwork.WxWorkMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBeanConfigs {

    @Value("${wxwork.agentId}")
    public String agentId;
    @Value("${wxwork.secret}")
    public String secret;
    @Value("${wxwork.corpId}")
    public String corpId;

    @Bean
    public WxWorkMessage wxWorkUtils() {
        return new WxWorkMessage(
                agentId,
                secret,
                corpId
        );
    }

}