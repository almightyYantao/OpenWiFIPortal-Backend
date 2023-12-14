package com.qunhe.its.networkportal.user.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qunhe.its.networkportal.user.mapper.PortalActMapper;
import com.qunhe.its.networkportal.user.mapper.PortalActUUIDMapper;
import com.qunhe.its.networkportal.user.model.ActScanConfirmReqVo;
import com.qunhe.its.networkportal.user.model.entry.PortalActEntry;
import com.qunhe.its.networkportal.user.model.entry.PortalActUUIDEntry;
import com.qunhe.its.networkportal.user.utils.PortalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @author yantao
 * @date 13.12.23 10:27
 */
@Service
@Slf4j
public class PortalActScanCodeService {

    @Autowired
    private PortalActSSEService portalActSseService;

    @Autowired
    private PortalActMapper actMapper;

    @Autowired
    private PortalActUUIDMapper actUUIDMapper;

    /**
     * 生成二维码
     *
     * @param qrCodeData
     * @return
     */
    public byte[] generateQRCodeImage(String qrCodeData, Integer width, Integer height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, width, height);

            BufferedImage qrCodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            qrCodeImage.createGraphics();

            Graphics2D graphics = (Graphics2D) qrCodeImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败", e);
            return new byte[0];
        }
    }

    /**
     * 员工确认提交
     *
     * @param vo
     * @throws IOException
     */
    public void confirm(ActScanConfirmReqVo vo) throws IOException {
        PortalCacheUtils.putCacheWxWork(vo.getUuid());
        // 新增扫码确认日志
        actMapper.insert(PortalActEntry.builder()
                .username(vo.getUsername())
                .customerName(vo.getCustomerName())
                .uuid(vo.getUuid())
                .build());
        // 更新 UUID 状态
        PortalActUUIDEntry actUUIDEntry = actUUIDMapper.get(vo.getUuid());
        actUUIDEntry.setStatus("SUCCESS");
        actUUIDMapper.update(actUUIDEntry);
        // 发送信息，扫码成功
        portalActSseService.sendByIp(vo.getIp(), String.format("SUCCESS|%s|%s", vo.getIp() + "|wxwork|" + vo.getUuid(), vo.getUuid()));
    }

}
