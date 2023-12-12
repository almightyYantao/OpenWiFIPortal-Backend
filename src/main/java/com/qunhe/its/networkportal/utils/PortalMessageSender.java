package com.qunhe.its.networkportal.utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Portal报文发送工具类
 */
@Getter
@Slf4j
public class PortalMessageSender {

    @Setter
    private int timeout = 3000;

    @Setter
    private Integer receiverPort = null;

    @Setter
    @NonNull
    private String acIp;

    @Setter
    @NonNull
    private Integer acPort;

    @Setter
    @NonNull
    DatagramSocket dataSocket;

    /**
     * 发送 Challenge 报文
     *
     * @param buff
     * @return
     * @throws IOException
     */
    public byte[] sendChallengeReqAuth(byte[] buff) throws IOException {
        // 创建Req_Challenge包
        byte[] reqChallenge = new byte[16];
        System.arraycopy(buff, 0, reqChallenge, 0, reqChallenge.length);
        reqChallenge[1] = (byte) 1;
        return sendSocket(reqChallenge);
    }

    /**
     * 发送认证报文
     *
     * @param inUsername
     * @param inPassword
     * @param buff
     * @return
     * @throws IOException
     */
    public byte[] sendReqAuthByChap(String inUsername, String inPassword, byte[] buff, byte[] reqId, byte[] challenge) throws IOException {
        byte[] username = inUsername.getBytes();
        byte[] password = inPassword.getBytes();

        // 生成 Chap 密码
        byte[] chapPassword = ChapEncryptUtils.generateChapPassword(reqId, challenge, password);

        // 构造认证信息的字节数组
        byte[] authBuff = new byte[4 + username.length + 4 + chapPassword.length];
        // AttrType: 1 (用户名)
        authBuff[0] = (byte) 1;
        // AttrLen: 长度为用户名长度+2
        authBuff[1] = (byte) (username.length + 2);
        // 拷贝用户名
        System.arraycopy(username, 0, authBuff, 2, username.length);
        // AttrType: 4 (经过 CHAP 加密的密码)
        authBuff[2 + username.length] = (byte) 4;
        // AttrLen: 长度为密码长度+2
        authBuff[3 + username.length] = (byte) (chapPassword.length + 2);
        // 拷贝 CHAP 密码
        System.arraycopy(chapPassword, 0, authBuff, 4 + username.length, chapPassword.length);
        // 创建 Req_Auth 包
        byte[] reqAuth = new byte[16 + authBuff.length];
        // 给 Req_Auth 包赋值
        // 拷贝前16个字节
        System.arraycopy(buff, 0, reqAuth, 0, 16);
        reqAuth[1] = (byte) 3;
        reqAuth[14] = (byte) 0;
        short atrAttrNum = 2;
        reqAuth[15] = (byte) atrAttrNum;
        // 拷贝认证信息
        System.arraycopy(authBuff, 0, reqAuth, 16, authBuff.length);
        return sendSocket(reqAuth);
    }


    /**
     * 发送认证报文
     *
     * @param inUsername
     * @param inPassword
     * @param buff
     * @return
     * @throws IOException
     */
    public byte[] sendReqAuthByPap(String inUsername, String inPassword, byte[] buff) throws IOException {
        byte[] username = inUsername.getBytes();
        byte[] password = inPassword.getBytes();

        // 构造认证信息的字节数组
        byte[] authBuff = new byte[4 + username.length + 4 + password.length];
        // AttrType: 1 (用户名)
        authBuff[0] = (byte) 1;
        // AttrLen: 长度为用户名长度+2
        authBuff[1] = (byte) (username.length + 2);
        // 拷贝用户名
        System.arraycopy(username, 0, authBuff, 2, username.length);
        // AttrType: 4 (经过 CHAP 加密的密码)
        authBuff[2 + username.length] = (byte) 4;
        // AttrLen: 长度为密码长度+2
        authBuff[3 + username.length] = (byte) (password.length + 2);
        // 拷贝 CHAP 密码
        System.arraycopy(password, 0, authBuff, 4 + username.length, password.length);
        // 创建 Req_Auth 包
        byte[] reqAuth = new byte[16 + authBuff.length];
        // 给 Req_Auth 包赋值
        // 拷贝前16个字节
        System.arraycopy(buff, 0, reqAuth, 0, 16);
        reqAuth[1] = (byte) 3;
        reqAuth[14] = (byte) 0;
        short atrAttrNum = 2;
        reqAuth[15] = (byte) atrAttrNum;
        // 拷贝认证信息
        System.arraycopy(authBuff, 0, reqAuth, 16, authBuff.length);
        return sendSocket(reqAuth);
    }

    /**
     * 发送报文
     *
     * @param msgBytes
     * @return
     * @throws IOException
     */
    public byte[] sendSocket(byte[] msgBytes) throws IOException {
        log.info("Request Message:{}", Arrays.toString(msgBytes));
        DatagramPacket requestPacket;
        // 创建发送数据包并发送给服务器
        requestPacket = new DatagramPacket(msgBytes, msgBytes.length, InetAddress.getByName(acIp), acPort);
        // 设置请求超时3秒
        dataSocket.setSoTimeout(timeout);
        dataSocket.send(requestPacket);

        byte[] ackData = new byte[16];
        // 接收服务器的数据包
        DatagramPacket receivePacket = new DatagramPacket(ackData, ackData.length);
        dataSocket.receive(receivePacket);

        byte[] ackAuthData = new byte[receivePacket.getLength()];
        System.arraycopy(ackData, 0, ackAuthData, 0, receivePacket.getLength());
        return ackAuthData;
    }

    /**
     * 发送报文，不回传
     *
     * @param msgBytes
     * @throws IOException
     */
    public void sendSocketNoResponse(byte[] msgBytes) throws IOException {
        log.info("Request Message:{}", Arrays.toString(msgBytes));
        DatagramPacket requestPacket;
        // 创建发送数据包并发送给服务器
        requestPacket = new DatagramPacket(msgBytes, msgBytes.length, InetAddress.getByName(acIp), acPort);
        // 设置请求超时3秒
        dataSocket.setSoTimeout(timeout);
        dataSocket.send(requestPacket);
    }

    /**
     * Double to Bytes
     *
     * @param d
     * @return
     */
    private byte[] doubleConvertBytes(Double d) {
        byte[] output = new byte[8];
        long lng = Double.doubleToLongBits(d);
        for (int i = 0; i < 8; i++) {
            output[i] = (byte) ((lng >> ((7 - i) * 8)) & 0xff);
        }
        return output;
    }

    /**
     * 初始化报文
     *
     * @param ip
     * @param portalVer
     * @param authType
     * @return
     */
    public byte[] init(String ip, int portalVer, int authType) {
        // 构建portal协议中的字段包
        byte[] ver = new byte[1];
        byte[] type = new byte[1];
        byte[] mod = new byte[1];
        byte[] rsvd = new byte[1];
        byte[] serialNo = doubleConvertBytes(65535 * Math.random());
        byte[] reqID = new byte[2];
        byte[] userIP = new byte[4];
        byte[] userPort = new byte[2];
        byte[] errCode = new byte[1];
        byte[] attrNum = new byte[1];
        byte[] challenge = new byte[16];

        /*
         * 给UserIP[]赋值 接收客户ip地址 IP地址压缩成4字节,如果要进一步处理的话,就可以转换成一个int了.
         */
        String[] ips = ip.split("[.]");
        // 将ip地址加入字段UserIP
        for (int i = 0; i < 4; i++) {
            int m = Integer.parseInt(ips[i]);
            byte b = (byte) m;
            userIP[i] = b;
        }

        ver[0] = (byte) portalVer;
        type[0] = (byte) 0;
        mod[0] = (byte) authType;
        rsvd[0] = (byte) 0;
        userPort[0] = (byte) 0;
        userPort[1] = (byte) 0;
        errCode[0] = (byte) 0;
        attrNum[0] = (byte) 0;

        // 创建Buff包
        byte[] buff = new byte[1024];
        // 给Buff包赋初始值
        buff[0] = ver[0];
        buff[1] = type[0];
        buff[2] = mod[0];
        buff[3] = rsvd[0];
        buff[4] = serialNo[0];
        buff[5] = serialNo[1];
        buff[8] = userIP[0];
        buff[9] = userIP[1];
        buff[10] = userIP[2];
        buff[11] = userIP[3];
        buff[12] = userPort[0];
        buff[13] = userPort[1];
        buff[14] = errCode[0];
        buff[15] = attrNum[0];
        return buff;
    }

    /**
     * 发送认证成功报文
     *
     * @param buff
     * @return
     * @throws IOException
     */
    public void sendAffAckReqAuth(byte[] buff) throws IOException {
        buff[1] = 7;
        buff[14] = 0;
        buff[15] = 0;
        sendSocketNoResponse(buff);
    }

    /**
     * 下线
     *
     * @param buff
     * @return
     * @throws IOException
     */
    public byte[] sendLogoutReqAuth(byte[] buff) throws IOException {
        buff[1] = 5;
        buff[14] = 0;
        buff[15] = 0;
        return sendSocket(buff);
    }

}