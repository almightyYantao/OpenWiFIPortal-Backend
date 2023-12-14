package com.qunhe.its.networkportal.user.service;

import com.qunhe.its.networkportal.admin.mapper.PortalSSIDMapper;
import com.qunhe.its.networkportal.admin.model.PortalSSIDEntry;
import com.qunhe.its.networkportal.user.mapper.PortalActUUIDMapper;
import com.qunhe.its.networkportal.user.model.entry.PortalActUUIDEntry;
import com.qunhe.its.networkportal.user.utils.PortalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yantao
 * @date 13.12.23 12:45
 */
@Service
@Slf4j
public class PortalActSSEService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Autowired
    private PortalActUUIDMapper actUUIDMapper;

    @Autowired
    private PortalSSIDMapper ssidMapper;

    /**
     * 二维码上线
     *
     * @param ip
     * @param uuid
     * @param ssid
     * @return
     */
    public SseEmitter online(String ip, String uuid, String ssid) {
        SseEmitter emitter = new SseEmitter();
        PortalSSIDEntry ssidEntry = ssidMapper.get(ssid);
        if (ssidEntry != null) {
            emitters.put(ip, emitter);
            PortalCacheUtils.putCacheWxWorkUnconfirmed(uuid);
            actUUIDMapper.insert(PortalActUUIDEntry.builder()
                    .uuid(uuid)
                    .status("UNCONFIRMED")
                    .build());
            // 设置连接超时时间
            emitter.onTimeout(() -> remove(ip, uuid));

            // 设置连接关闭时的处理
            emitter.onCompletion(() -> remove(ip, uuid));
            // 定义任务
            Runnable task = () -> {
                SseEmitter expiredEmitter = emitters.get(ip);
                if (expiredEmitter != null) {
                    try {
                        expiredEmitter.send(SseEmitter.event().data("The QR code has expired"));
                        expiredEmitter.complete();
                    } catch (IOException e) {
                        log.error("过期消息失败", e);
                    }
                }
                remove(ip, uuid);
            };
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            // 延迟1分钟后执行任务
            scheduler.schedule(task, 1, TimeUnit.MINUTES);
        } else {
            log.error("SSID 不正确/不存在");
        }
        return emitter;
    }

    /**
     * 移除用户
     *
     * @param ip
     * @param uuid
     */
    public void remove(String ip, String uuid) {
        PortalCacheUtils.delCacheWxWork(uuid);
        log.info("二维码失效:{}", uuid);
        PortalActUUIDEntry actUUIDEntry = actUUIDMapper.get(uuid);
        actUUIDEntry.setStatus("EFFECTIVENESS");
        actUUIDMapper.update(actUUIDEntry);
        emitters.remove(ip);
    }


    public void sendByIp(String ip, String content) throws IOException {
        emitters.get(ip).send(SseEmitter.event().data(content));
    }
}
