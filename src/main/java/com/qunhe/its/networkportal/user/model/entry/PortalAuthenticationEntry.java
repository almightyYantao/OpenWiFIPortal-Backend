package com.qunhe.its.networkportal.user.model.entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class PortalAuthenticationEntry {

    private Integer id;

    private String ip;

    private String mac;

    private Long created;

    private String type;

    private Boolean status;

    private String sessionId;

}
