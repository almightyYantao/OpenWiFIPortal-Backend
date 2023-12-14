package com.qunhe.its.networkportal.common.wxwork.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author yantao
 * @date 14.12.23 13:23
 */
@Data
public class UserInfoResponse {

    @JsonProperty("errcode")
    private int errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    @JsonProperty("userid")
    private String userId;

    @JsonProperty("user_ticket")
    private int userTicket;
}
