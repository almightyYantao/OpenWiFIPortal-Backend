package com.qunhe.its.networkportal.user.controller;

import com.qunhe.its.networkportal.common.InvokeResult;
import com.qunhe.its.networkportal.common.annotation.ExceptionResponse;
import com.qunhe.its.networkportal.user.model.ActScanConfirmReqVo;
import com.qunhe.its.networkportal.user.service.PortalActScanCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

/**
 * @author yantao
 * @date 13.12.23 11:05
 */
@RestController
@RequestMapping("/api/v1/portal/act")
@Slf4j
public class PortalActQRCodeController {

    @Autowired
    private PortalActScanCodeService portalActScanCodeService;

    @Value("${act.qrcode.url:#{null}}")
    private String qrcodeUrl;

    @GetMapping("/generateQRCode")
    public String generateQRCode(Model model) {
        // Generate QR Code
        String qrCodeData = qrcodeUrl;
        byte[] qrCodeImage = portalActScanCodeService.generateQRCodeImage(qrCodeData, 200, 200);
        // Pass QR Code image to the view
        model.addAttribute("qrCodeImage", qrCodeImage);
        return Base64.getEncoder().encodeToString(qrCodeImage);
    }

    @PostMapping("/confirm")
    @ExceptionResponse
    public InvokeResult confirm(@RequestBody ActScanConfirmReqVo vo) throws IOException {
        portalActScanCodeService.confirm(vo);
        return InvokeResult.ok();
    }

}
