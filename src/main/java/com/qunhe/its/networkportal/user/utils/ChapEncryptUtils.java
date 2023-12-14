package com.qunhe.its.networkportal.user.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Chap加密
 */
@Slf4j
public class ChapEncryptUtils {


    /**
     * 生成 Chap 密码
     *
     * @param reqId
     * @param challenge
     * @param usp
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] generateChapPassword(byte[] reqId, byte[] challenge, byte[] usp) throws UnsupportedEncodingException {

        byte chapPwd[] = new byte[16];
        // 初始化chapPassword byte[]
        byte[] buf = new byte[1 + usp.length + challenge.length];
        // 给 chapPassword byte[] 传值
        /*
         * Chap_Password的生成：Chap_Password的生成遵循标准的Radious协议中的Chap_Password 生成方法（参见RFC2865）。
         * 密码加密使用MD5算法，MD5函数的输入为ChapID ＋ Password ＋challenge
         * 其中，ChapID取ReqID的低 8 位，Password的长度不够协议规定的最大长度，其后不需要补零。
         */
        buf[0] = reqId[1];
        System.arraycopy(usp, 0, buf, 1, usp.length);
        System.arraycopy(challenge, 0, buf, 1 + usp.length, challenge.length);

        //生成Chap-Password
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(buf);
            chapPwd = md.digest();
        } catch (NoSuchAlgorithmException e) {
            log.error("生成Chap-Password出错！", e);
        }
        return chapPwd;
    }

}