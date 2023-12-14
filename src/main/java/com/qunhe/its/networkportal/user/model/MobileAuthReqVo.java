package com.qunhe.its.networkportal.user.model;


import lombok.Data;

@Data
public class MobileAuthReqVo {
    /**
     * 登录用户名
     */
    private String loginName;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 用户 Mac
     */
    private String deviceMac;
    /**
     * 短信验证码
     */
    private Integer code;
    /**
     * 类型
     */
    private String type;

    private String uuid;
}
