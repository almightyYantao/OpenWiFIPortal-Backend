package com.qunhe.its.networkportal.user.service;

import com.qunhe.its.networkportal.common.PortalAcErrorCode;
import com.qunhe.its.networkportal.common.PortalException;
import com.qunhe.its.networkportal.user.mapper.PortalAcctOnlineMapper;
import com.qunhe.its.networkportal.user.mapper.PortalAuthenticationMapper;
import com.qunhe.its.networkportal.user.model.MobileAuthReqVo;
import com.qunhe.its.networkportal.user.model.entry.PortalAcctOnlineEntry;
import com.qunhe.its.networkportal.user.utils.PortalCacheUtils;
import com.qunhe.its.networkportal.user.utils.PortalMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Objects;

/**
 * @author yantao
 * @date 8.12.23 17:04
 */
@Service
@Slf4j
public class PortalLoginService {


    @Value("${nas.ip}")
    private String acIp;

    @Value("${nas.port}")
    private Integer nasPort;

    @Autowired
    private MessageSmsService messageSmsService;

    @Autowired
    private PortalAuthenticationMapper portalAuthenticationMapper;

    @Autowired
    private PortalAcctOnlineMapper acctOnlineMapper;

    /**
     * 登录认证
     *
     * @param vo
     * @throws IOException
     */
    public void loginByChap(MobileAuthReqVo vo) throws IOException, PortalException {
        if (Objects.equals(vo.getType(), "sms")) {
            if (!messageSmsService.verificationCode(vo.getLoginName().replace("|sms", ""), vo.getCode())) {
                throw new PortalException(PortalAcErrorCode.SMS_MESSAGE_ERROR);
            }
            PortalCacheUtils.verificationSuccessCache(vo.getLoginName());
        }
        if (Objects.equals(vo.getType(), "wxwork")) {
            if (!"SUCCESS".equals(PortalCacheUtils.getCacheWxWork(vo.getUuid()))) {
                throw new PortalException(PortalAcErrorCode.WXWORK_MESSAGE_ERROR);
            }
            PortalCacheUtils.delCacheWxWork(vo.getUuid());
        }
        log.info("Received UserInfo:{}", vo);
        PortalMessageUtils sender = new PortalMessageUtils();
        byte[] buff = sender.init(vo.getUserIp(), 1, 0, 1024);
        sender.setTimeout(3000);
        sender.setAcIp(acIp);
        sender.setAcPort(nasPort);
        sender.setDataSocket(new DatagramSocket());
        // 发送 Challenge 包，进行校验
        byte[] ackData = sender.sendChallengeReqAuth(buff);
        if (!PortalAcErrorCode.parse(ackData).equals(PortalAcErrorCode.CHALLENGE_REQUEST)) {
            throw new PortalException(PortalAcErrorCode.parse(ackData));
        }
        // 给Buff包赋值
        System.arraycopy(ackData, 0, buff, 0, ackData.length);
        byte[] challenge = new byte[16];
        byte[] reqId = new byte[2];
        reqId[0] = buff[6];
        reqId[1] = buff[7];
        System.arraycopy(buff, 18, challenge, 0, challenge.length);
        // 发送认证请求报文
        ackData = sender.sendReqAuthByChap(vo.getLoginName(), vo.getPassword(), buff, reqId, challenge);
        if (!PortalAcErrorCode.parse(ackData).equals(PortalAcErrorCode.USER_AUTH_SUCCESSFUL)
                && !PortalAcErrorCode.parse(ackData).equals(PortalAcErrorCode.USER_AUTH_CONNECTION_ESTABLISHED)) {
            throw new PortalException(PortalAcErrorCode.parse(ackData));
        }
        sender.sendAffAckReqAuth(buff);
        sender.getDataSocket().close();
        sender.getDataSocket().disconnect();
    }


    /**
     * 登录认证
     *
     * @param vo
     * @throws IOException
     */
    public void loginByPap(MobileAuthReqVo vo) throws IOException, PortalException {
        log.info("Received UserInfo:{}", vo);
        PortalMessageUtils sender = new PortalMessageUtils();
        byte[] buff = sender.init(vo.getUserIp(), 1, 1, 1024);
        sender.setTimeout(300000);
        sender.setAcIp(acIp);
        sender.setAcPort(nasPort);
        sender.setDataSocket(new DatagramSocket());
        // 发送认证请求报文
        byte[] ackData = sender.sendReqAuthByPap(vo.getLoginName(), vo.getPassword(), buff);
        if (!PortalAcErrorCode.parse(ackData).equals(PortalAcErrorCode.USER_AUTH_SUCCESSFUL)
                && !PortalAcErrorCode.parse(ackData).equals(PortalAcErrorCode.USER_AUTH_CONNECTION_ESTABLISHED)) {
            throw new PortalException(PortalAcErrorCode.parse(ackData));
        }
        sender.getDataSocket().close();
        sender.getDataSocket().disconnect();
    }

    /**
     * 退出登录
     *
     * @param vo
     * @throws IOException
     */
    public void logout(MobileAuthReqVo vo) throws IOException {
        PortalMessageUtils sender = new PortalMessageUtils();
        byte[] buff = sender.init(vo.getUserIp(), 1, 0, 16);
        sender.setTimeout(3000);
        sender.setAcIp(acIp);
        sender.setAcPort(nasPort);
        sender.setDataSocket(new DatagramSocket());
        sender.sendLogoutReqAuth(buff);
    }

    /**
     * 强制下线
     *
     * @param vo
     * @throws IOException
     */
    public void forceLogout(MobileAuthReqVo vo) throws IOException {
        PortalMessageUtils sender = new PortalMessageUtils();
        byte[] buff = sender.init(vo.getUserIp(), 1, 0, 16);
        sender.setTimeout(3000);
        sender.setAcIp(acIp);
        sender.setAcPort(nasPort);
        sender.setDataSocket(new DatagramSocket());
        sender.sendLogoutReqAuth(buff);
        portalAuthenticationMapper.downlineByIpAndMac(vo.getUserIp(), vo.getDeviceMac());
        List<PortalAcctOnlineEntry> acctOnlineEntrys = acctOnlineMapper.getOnlineByIp(vo.getUserIp());
        for (PortalAcctOnlineEntry acctOnlineEntry : acctOnlineEntrys) {
            acctOnlineMapper.updateEndTime(acctOnlineEntry);
        }
    }
}
