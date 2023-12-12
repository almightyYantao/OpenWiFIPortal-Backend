package com.qunhe.its.networkportal.common;

/**
 * 自定义异常类，用于表示与网络门户相关的异常
 */
public class PortalException extends Exception {

    public PortalException(PortalAcErrorCode errorStatus) {
        super("Portal error: " + errorStatus.getDescription());
    }
}
