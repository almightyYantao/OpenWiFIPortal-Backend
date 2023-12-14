package com.qunhe.its.networkportal.admin.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yantao
 * @date 14.12.23 15:26
 */
@Data
public class PortalSSIDEntry {

    private Integer id;

    private String ssid;

    private Timestamp created;

    private Timestamp lastUpdate;

}
