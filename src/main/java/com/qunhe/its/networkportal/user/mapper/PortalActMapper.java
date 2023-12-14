package com.qunhe.its.networkportal.user.mapper;

import com.qunhe.its.networkportal.user.model.entry.PortalActEntry;

/**
 * @author yantao
 * @date 14.12.23 11:45
 */
public interface PortalActMapper {

    void insert(PortalActEntry act);

    PortalActEntry get(String uuid);

}
