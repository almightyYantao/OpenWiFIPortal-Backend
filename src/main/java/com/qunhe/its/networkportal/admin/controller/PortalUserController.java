package com.qunhe.its.networkportal.admin.controller;

import com.qunhe.its.networkportal.common.InvokeResult;
import com.qunhe.its.networkportal.common.annotation.ExceptionResponse;
import com.qunhe.its.networkportal.user.model.MobileAuthReqVo;
import com.qunhe.its.networkportal.user.service.PortalLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author yantao
 * @date 14.12.23 14:50
 */
@RestController
@RequestMapping("/api/v1/admin/portal")
@Slf4j
public class PortalUserController {


    @Autowired
    private PortalLoginService portalLoginService;

    @PostMapping("/logout")
    @ExceptionResponse
    public InvokeResult logout(
            @RequestBody MobileAuthReqVo vo
    ) throws IOException {
        portalLoginService.logout(vo);
        return InvokeResult.ok();
    }

    @PostMapping("/forceLogout")
    @ExceptionResponse
    public InvokeResult forceLogout(
            @RequestBody MobileAuthReqVo vo
    ) throws IOException {
        portalLoginService.forceLogout(vo);
        return InvokeResult.ok();
    }
}
