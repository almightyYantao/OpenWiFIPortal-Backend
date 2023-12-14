package com.qunhe.its.networkportal.user.controller;

import com.qunhe.its.networkportal.common.InvokeResult;
import com.qunhe.its.networkportal.common.annotation.ExceptionResponse;
import com.qunhe.its.networkportal.user.model.MobileSmsCodeReqVo;
import com.qunhe.its.networkportal.user.service.MessageSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author yantao
 * @date 12.12.23 16:38
 */
@RestController
@RequestMapping("/api/v1/portal/sms")
@Slf4j
public class PortalSmsController {

    @Autowired
    private MessageSmsService messageSmsService;

    @PostMapping()
    @ExceptionResponse
    public InvokeResult sendSms(
            @RequestBody MobileSmsCodeReqVo vo
    ) throws IOException {
        messageSmsService.sendSms(vo);
        return InvokeResult.ok();
    }

}
