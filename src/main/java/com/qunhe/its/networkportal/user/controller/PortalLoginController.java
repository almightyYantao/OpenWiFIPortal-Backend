package com.qunhe.its.networkportal.user.controller;

import com.qunhe.its.networkportal.common.InvokeResult;
import com.qunhe.its.networkportal.common.PortalException;
import com.qunhe.its.networkportal.common.annotation.ExceptionResponse;
import com.qunhe.its.networkportal.user.model.MobileAuthReqVo;
import com.qunhe.its.networkportal.user.service.PortalLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author yantao
 * @date 1.12.23 15:11
 */
@RestController
@RequestMapping("/api/v1/portal")
@Slf4j
public class PortalLoginController {

    @Autowired
    private PortalLoginService portalLoginService;

    @PostMapping("/login")
    @ExceptionResponse
    public InvokeResult login(
            @RequestBody MobileAuthReqVo vo
    ) throws IOException, PortalException {
        portalLoginService.loginByChap(vo);
        return InvokeResult.ok();
    }
}
