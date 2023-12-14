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
 * @date 14.12.23 11:42
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortalActEntry {

    private Integer id;

    private String username;

    private String customerName;

    private String uuid;

    private Timestamp created;

    private Timestamp lastUpdate;

}
