package com.qunhe.its.networkportal.model;


import lombok.Data;

@Data
public class MobileAuthReqVo {
    private String loginName;
    private String password;
    private String userIp;
    private String deviceMac;
}
