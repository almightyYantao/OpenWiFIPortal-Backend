package com.qunhe.its.networkportal.user.controller;

import com.qunhe.its.networkportal.common.annotation.ExceptionResponse;
import com.qunhe.its.networkportal.common.wxwork.WxWorkMessage;
import com.qunhe.its.networkportal.common.InvokeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author yantao
 * @date 14.12.23 13:45
 */
@RestController
@RequestMapping("/api/v1/portal/wxwork")
@Slf4j
public class PortalWxWorkController {

    @Autowired
    private WxWorkMessage wxWorkMessage;

    @GetMapping("/code")
    @ExceptionResponse
    public InvokeResult code2user(@RequestParam("code") String code) throws IOException {
        return InvokeResult.ok(wxWorkMessage.codeToUser(code));
    }

}
