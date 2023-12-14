package com.qunhe.its.networkportal.common.wxwork;

import com.alibaba.fastjson.JSONObject;
import com.qunhe.its.networkportal.common.HttpUtil;
import com.qunhe.its.networkportal.common.wxwork.model.AccessTokenResponse;
import com.qunhe.its.networkportal.common.wxwork.model.UserInfoResponse;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * @author yantao
 * @date 14.12.23 13:18
 */
public class WxWorkMessage {
    private static AccessTokenResponse accessTokenResponse;
    private static Instant lastFetchTime;
    @NonNull
    public String agentId;
    @NonNull
    public String secret;
    @NonNull
    public String corpId;

    public WxWorkMessage(@NotNull String agentId, @NotNull String secret, @NotNull String corpId) {
        this.agentId = agentId;
        this.corpId = corpId;
        this.secret = secret;
    }

    private static final HttpUtil HTTP_UTIL = new HttpUtil();

    /**
     * 缓存信息
     *
     * @return
     * @throws IOException
     */
    @Cacheable(value = "accessTokenCache", key = "'wxworkAccessToken'")
    public String getCacheAccessToken() throws IOException {
        if (accessTokenResponse == null || isExpired()) {
            // 如果AccessToken为null或者已过期，重新获取
            getAccessToken();
        }
        return accessTokenResponse.getAccessToken();
    }

    /**
     * 清理缓存
     */
    @CacheEvict(value = "accessTokenCache", key = "'wxworkAccessToken'")
    public void evictAccessTokenCache() {
    }

    /**
     * 判断有效期是否过期
     *
     * @return
     */
    private static boolean isExpired() {
        // 计算当前时间与上次获取的时间之间的时长
        Duration duration = Duration.between(lastFetchTime, Instant.now());
        // 判断时长是否超过 AccessToken 的有效期
        return duration.getSeconds() >= accessTokenResponse.getExpiresIn();
    }

    /**
     * 获取企业微信 Token
     *
     * @return
     * @throws IOException
     */
    private void getAccessToken() throws IOException {
        // https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRET

        String response = HTTP_UTIL.doGet(
                String.format(
                        "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s"
                        , this.corpId
                        , this.secret
                )
                , Collections.emptyMap()
        );
        accessTokenResponse = JSONObject.parseObject(response, AccessTokenResponse.class);
        if (accessTokenResponse.getErrCode() != 0) {
            throw new IOException(accessTokenResponse.getErrMsg());
        }
        lastFetchTime = Instant.now();
        this.evictAccessTokenCache();
    }

    /**
     * Code 换取花名拼音
     *
     * @param code
     * @return
     * @throws IOException
     */
    public String codeToUser(String code) throws IOException {
        String response = HTTP_UTIL.doGet(String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=%s&code=%s"
                , getCacheAccessToken()
                , code
        ), Collections.emptyMap());
        UserInfoResponse userInfoResponse = JSONObject.parseObject(response, UserInfoResponse.class);
        if (userInfoResponse.getErrCode() != 0) {
            throw new IOException(userInfoResponse.getErrMsg());
        }
        return userInfoResponse.getUserId();
    }

}
