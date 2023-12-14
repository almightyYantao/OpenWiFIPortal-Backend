package com.qunhe.its.networkportal.user.mapper;

import com.qunhe.its.networkportal.user.model.entry.PortalAuthenticationEntry;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yantao
 * @date 13.12.23 15:45
 */
@Mapper
public interface PortalAuthenticationMapper {

    void insert(PortalAuthenticationEntry entry);

    int downlineByIpAndMac(String ip, String mac);

    PortalAuthenticationEntry selectByIpAndSessionId(String ip, String sessionId);

}
