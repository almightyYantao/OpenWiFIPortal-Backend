package com.qunhe.its.networkportal.service;

import com.qunhe.its.networkportal.common.PortalAcErrorCode;
import com.qunhe.its.networkportal.common.PortalException;
import com.qunhe.its.networkportal.model.MobileAuthReqVo;
import com.qunhe.its.networkportal.utils.PortalMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * @author yantao
 * @date 8.12.23 17:04
 */
@Service
@Slf4j
public class LoginService {

    private static final String AC_IP = "10.10.200.55";
    private static final Integer AC_PORT = 2000;

    /**
     * 登录认证
     *
     * @param vo
     * @throws IOException
     */
    public void loginByChap(MobileAuthReqVo vo) throws IOException, PortalException {
        log.info("Received UserInfo:{}", vo);
        PortalMessageSender sender = new PortalMessageSender();
        byte[] buff = sender.init(vo.getUserIp(), 1, 0);
        sender.setTimeout(3000);
        sender.setAcIp(AC_IP);
        sender.setAcPort(AC_PORT);
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
        PortalMessageSender sender = new PortalMessageSender();
        byte[] buff = sender.init(vo.getUserIp(), 1, 1);
        sender.setTimeout(300000);
        sender.setAcIp(AC_IP);
        sender.setAcPort(AC_PORT);
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
        PortalMessageSender sender = new PortalMessageSender();
        byte[] buff = sender.init(vo.getUserIp(), 1, 0);
        sender.setTimeout(3000);
        sender.setAcIp(AC_IP);
        sender.setAcPort(AC_PORT);
        sender.setDataSocket(new DatagramSocket());
        sender.sendLogoutReqAuth(buff);
    }
}
