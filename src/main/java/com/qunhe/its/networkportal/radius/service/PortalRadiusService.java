package com.qunhe.its.networkportal.radius.service;

import com.qunhe.its.networkportal.user.mapper.PortalAcctOnlineMapper;
import com.qunhe.its.networkportal.user.mapper.PortalAuthenticationMapper;
import com.qunhe.its.networkportal.user.model.entry.PortalAcctOnlineEntry;
import com.qunhe.its.networkportal.user.model.entry.PortalAuthenticationEntry;
import com.qunhe.its.networkportal.user.utils.PortalCacheUtils;
import com.qunhe.its.networkportal.user.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
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
public class PortalRadiusService {

    private static final String SECRET = PropertiesUtils.properties.getProperty("radius.secret");

    @Autowired
    private PortalAuthenticationMapper portalAuthenticationMapper;

    @Autowired
    private PortalAcctOnlineMapper portalAcctOnlineMapper;

    @Bean
    public RadiusServer radiusServer() {
        RadiusServer radiusServer = new RadiusServer() {
            @Override
            public String getSharedSecret(InetSocketAddress inetSocketAddress) {
                return SECRET;
            }

            @Override
            public String getUserPassword(String s) {
                return String.valueOf(PortalCacheUtils.getVerificationCode(s));
            }

            /**
             * 监听接收到的认证报文
             * @param accessRequest
             * @param client
             * @return
             * @throws RadiusException
             */
            public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
                log.info("Received Access-Request:\n" + accessRequest);
                RadiusPacket packet = new RadiusPacket(RadiusPacket.ACCESS_REJECT, accessRequest.getPacketIdentifier());
                this.copyProxyState(accessRequest, packet);
                String[] usernameParam = accessRequest.getUserName().split("\\|");
                String ip = accessRequest.getAttributeValue("Framed-IP-Address");
                String sessionId = accessRequest.getAttributeValue("Acct-Session-Id");
                String mac = accessRequest.getAttributeValue("Calling-Station-Id").replace("-", "");
                accessRequest.setUserName(usernameParam[0]);
                if (usernameParam.length > 1) {
                    switch (usernameParam[1]) {
                        case "wxwork":
                            accessRequest.setAuthProtocol("pap");
                            accessRequest.setUserPassword(usernameParam[2]);
                            break;
                        case "sms":
                            accessRequest.setAuthProtocol("pap");
                            String plaintext = this.getUserPassword(usernameParam[0]);
                            accessRequest.setUserPassword(this.getUserPassword(usernameParam[0]));
                            if (plaintext != null && accessRequest.verifyPassword(plaintext)) {
                                packet.setPacketType(RadiusPacket.ACCESS_ACCEPT);
                            }
                            break;
                        default:
                            usernameParam[1] = "account";
                            break;
                    }
                }
                PortalAuthenticationEntry portalAuthenticationEntry = portalAuthenticationMapper.selectByIpAndSessionId(ip, sessionId);
                if (portalAuthenticationEntry != null) {
                    packet.setPacketType(RadiusPacket.ACCESS_REJECT);
                    packet.addAttribute("Reply-Message", "用户已上线，请不要重复上线");
                    return packet;
                }
                // 插入认证记录
                portalAuthenticationMapper.insert(PortalAuthenticationEntry.builder()
                        .ip(ip)
                        .mac(mac)
                        .type(usernameParam[1])
                        .sessionId(sessionId)
                        .build());
                return packet;
            }

            /**
             * 监听计费报文
             * @param accountingRequest
             * @param client
             * @return
             * @throws RadiusException
             */
            public RadiusPacket accountingRequestReceived(final AccountingRequest accountingRequest,
                                                          final InetSocketAddress client) throws RadiusException {
                log.info("Received Accounting-Request:\n" + accountingRequest);
                RadiusPacket packet = new RadiusPacket(RadiusPacket.ACCOUNTING_RESPONSE, accountingRequest.getPacketIdentifier());
                this.copyProxyState(accountingRequest, packet);
                String ip = accountingRequest.getAttributeValue("Framed-IP-Address");
                String sessionId = accountingRequest.getAttributeValue("Acct-Session-Id");
                String nasPort = accountingRequest.getAttributeValue("NAS-Port");
                String nasIpAddress = accountingRequest.getAttributeValue("NAS-IP-Address");
                String mac = accountingRequest.getAttributeValue("Calling-Station-Id").replace("-", "");
                String status = accountingRequest.getAttributeValue("Acct-Status-Type");
                PortalAcctOnlineEntry acctOnlineEntry = portalAcctOnlineMapper.getOnline(ip, sessionId);
                if ("Stop".equals(status)) {
                    portalAuthenticationMapper.downlineByIpAndMac(ip, mac);
                    if (acctOnlineEntry != null) {
                        portalAcctOnlineMapper.updateEndTime(acctOnlineEntry);
                    }
                } else if ("Start".equals(status)) {
                    if (acctOnlineEntry != null) {
                        packet.setPacketType(RadiusPacket.ACCESS_REJECT);
                        packet.addAttribute("Reply-Message", "用户已上线");
                        return packet;
                    } else {
                        portalAcctOnlineMapper.insert(
                                PortalAcctOnlineEntry.builder()
                                        .ip(ip)
                                        .mac(mac)
                                        .sessionId(sessionId)
                                        .nasPort(Integer.parseInt(nasPort))
                                        .nasIp(nasIpAddress)
                                        .username(accountingRequest.getUserName())
                                        .build()
                        );
                    }
                }
                return packet;
            }
        };
        // 启动Radius服务器
        radiusServer.start(true, true);
        log.info("=======>Radius Server started<=======");
        return radiusServer;
    }
}
