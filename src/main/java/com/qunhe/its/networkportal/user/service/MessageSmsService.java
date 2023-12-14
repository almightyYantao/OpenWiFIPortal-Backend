package com.qunhe.its.networkportal.user.service;

import com.alibaba.fastjson.JSONObject;
import com.qunhe.its.networkportal.user.model.MobileSmsCodeReqVo;
import com.qunhe.its.networkportal.common.HttpUtil;
import com.qunhe.its.networkportal.user.utils.PortalCacheUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yantao
 * @date 12.12.23 16:28
 */
@Service
public class MessageSmsService {

    /**
     * 发送短信验证码
     *
     * @param mobileSmsCodeReqVo
     * @throws IOException
     */
    public void sendSms(MobileSmsCodeReqVo mobileSmsCodeReqVo) throws IOException {
        HttpUtil httpUtil = new HttpUtil();
        Map<String, Object> json = new HashMap<>();
        json.put("phone", mobileSmsCodeReqVo.getPhone());
        int verificationCode = PortalCacheUtils.generateAndCacheVerificationCode(mobileSmsCodeReqVo.getPhone());
        json.put("content", "验证码" + verificationCode + "是您本次的 Wi-Fi 登录凭证，2 分钟内有效，请您查收");
        httpUtil.doPostJson("https://complex.qunhequnhe.com/api/v1/visitor/send", JSONObject.toJSONString(json), new HashMap<>());
    }

    /**
     * 验证验证码是否正确
     *
     * @param phone
     * @param code
     * @return
     */
    public Boolean verificationCode(String phone, int code) {
        Integer verificationCode = PortalCacheUtils.getVerificationCode(phone);
        if (verificationCode == null) {
            return false;
        }
        return code == verificationCode;
    }

}
