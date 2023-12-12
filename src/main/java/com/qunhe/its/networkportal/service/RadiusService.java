package com.qunhe.its.networkportal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;

import java.net.InetSocketAddress;

/**
 * @author yantao
 * @date 12.12.23 09:23
 */
@Component
@Slf4j
public class RadiusService {

    private static final String SECRET = "qunhequnhe";

    @Bean
    public RadiusServer radiusServer() {
        // 创建一个Radius服务器
        RadiusServer radiusServer = new RadiusServer() {
            @Override
            public String getSharedSecret(InetSocketAddress inetSocketAddress) {
                return SECRET;
            }

            @Override
            public String getUserPassword(String s) {
                return null;
            }

            public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
                log.info("Received Access-Request:\n" + accessRequest);
                String plaintext = this.getUserPassword(accessRequest.getUserName());
                int type = 2;
                if (plaintext != null && accessRequest.verifyPassword(plaintext)) {
                    type = 2;
                }
                RadiusPacket answer = new RadiusPacket(type, accessRequest.getPacketIdentifier());
                this.copyProxyState(accessRequest, answer);
                return answer;
            }
        };
        // 启动Radius服务器
        radiusServer.start(true, true);
        log.info("=======>Radius Server started<=======");
        return radiusServer;
    }

    private RadiusPacket createResponse(AccessRequest accessRequest, boolean authenticationResult) {
        // 创建一个RadiusPacket对象，表示响应
        RadiusPacket response = new RadiusPacket(2, accessRequest.getPacketIdentifier());

        // 根据认证结果设置相应的属性
        if (authenticationResult) {
            // 认证成功，返回Access-Accept
            response.addAttribute("Framed-IP-Address", "192.168.1.100");
            // 添加其他属性...
        } else {
            // 认证失败，返回Access-Reject
            response = new RadiusPacket(3, accessRequest.getPacketIdentifier());
        }

        return response;
    }

    private RadiusPacket createRejectResponse(AccessRequest accessRequest) {
        // 创建一个RadiusPacket对象，表示拒绝响应
        RadiusPacket response = new RadiusPacket(3, accessRequest.getPacketIdentifier());
        return response;
    }
}
