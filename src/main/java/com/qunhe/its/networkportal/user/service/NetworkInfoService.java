package com.qunhe.its.networkportal.user.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @author yantao
 * @date 14.12.23 10:52
 */
@Service
public class NetworkInfoService {

    /**
     * 获取用户 IP 地址
     *
     * @return
     */
    public String getClientIpAddress() {
        // 获取当前请求的 HttpServletRequest
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 获取客户端的 IP 地址
        String clientIpAddress = request.getHeader("X-Forwarded-For");
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("Proxy-Client-IP");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("HTTP_FORWARDED");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("HTTP_VIA");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getHeader("REMOTE_ADDR");
        }
        if (clientIpAddress == null || clientIpAddress.isEmpty() || "unknown".equalsIgnoreCase(clientIpAddress)) {
            clientIpAddress = request.getRemoteAddr();
        }

        return clientIpAddress;
    }

}
