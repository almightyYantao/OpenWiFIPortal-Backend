package com.qunhe.its.networkportal.user.model.entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author yantao
 * @date 13.12.23 15:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortalAcctOnlineEntry {

    private Integer id;

    /**
     * 创建时间
     */
    private Timestamp created;
    /**
     * 最后更新时间
     */
    private Timestamp lastUpdate;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * IP
     */
    private String ip;
    /**
     * Mac
     */
    private String mac;
    /**
     * NAS 的 Port
     */
    private Integer nasPort;
    private String nasIp;
    /**
     * Session ID
     */
    private String sessionId;
    /**
     * 用户名
     */
    private String username;

}
