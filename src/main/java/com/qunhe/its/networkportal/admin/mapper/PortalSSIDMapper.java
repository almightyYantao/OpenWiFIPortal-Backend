package com.qunhe.its.networkportal.admin.mapper;

import com.qunhe.its.networkportal.admin.model.PortalSSIDEntry;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yantao
 * @date 14.12.23 15:24
 */
@Mapper
public interface PortalSSIDMapper {

    PortalSSIDEntry get(String ssid);

}
