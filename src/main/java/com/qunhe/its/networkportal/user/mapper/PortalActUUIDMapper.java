package com.qunhe.its.networkportal.user.mapper;

import com.qunhe.its.networkportal.user.model.entry.PortalActUUIDEntry;

/**
 * @author yantao
 * @date 14.12.23 11:45
 */
public interface PortalActUUIDMapper {

    void insert(PortalActUUIDEntry act);

    PortalActUUIDEntry get(String uuid);

    void update(PortalActUUIDEntry act);

}
