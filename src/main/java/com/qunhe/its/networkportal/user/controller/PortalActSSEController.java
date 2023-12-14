package com.qunhe.its.networkportal.user.controller;

import com.qunhe.its.networkportal.user.service.PortalActSSEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/portal/sse/events")
public class PortalActSSEController {

    @Autowired
    private PortalActSSEService service;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(
            @RequestParam("ip") String ip,
            @RequestParam("uuid") String uuid,
            @RequestParam("ssid") String ssid
    ) {
        return service.online(ip, uuid, ssid);
    }
}