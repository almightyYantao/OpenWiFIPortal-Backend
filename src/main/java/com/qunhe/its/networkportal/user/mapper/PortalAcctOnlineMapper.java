package com.qunhe.its.networkportal.user.mapper;

import com.qunhe.its.networkportal.user.model.entry.PortalAcctOnlineEntry;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author yantao
 * @date 13.12.23 15:45
 */
@Mapper
public interface PortalAcctOnlineMapper {

    PortalAcctOnlineEntry getOnline(String ip, String sessionId);

    List<PortalAcctOnlineEntry> getOnlineByIp(String ip);

    void insert(PortalAcctOnlineEntry entry);

    void updateEndTime(PortalAcctOnlineEntry entry);

}
