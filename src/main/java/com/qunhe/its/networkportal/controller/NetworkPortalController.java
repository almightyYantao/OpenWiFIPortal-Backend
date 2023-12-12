package com.qunhe.its.networkportal.controller;

import com.qunhe.its.networkportal.model.InvokeResult;
import com.qunhe.its.networkportal.model.MobileAuthReqVo;
import com.qunhe.its.networkportal.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yantao
 * @date 1.12.23 15:11
 */
@RestController
@RequestMapping("/api/v1/portal/login")
@CrossOrigin("*")
@Slf4j
public class NetworkPortalController {

    @Autowired
    private LoginService loginService;

    @PostMapping()
    public InvokeResult login(
            @RequestBody MobileAuthReqVo vo
    ) {
        try {
            loginService.loginByChap(vo);
            return InvokeResult.ok();
        } catch (Exception e) {
            log.error("认证失败", e);
            return InvokeResult.failure(e.getMessage());
        }
    }

    @DeleteMapping()
    public InvokeResult logout(
            @RequestBody MobileAuthReqVo vo
    ) {
        try {
            loginService.logout(vo);
            return InvokeResult.ok();
        } catch (Exception e) {
            log.error("认证失败", e);
            return InvokeResult.failure(e.getMessage());
        }
    }
}
