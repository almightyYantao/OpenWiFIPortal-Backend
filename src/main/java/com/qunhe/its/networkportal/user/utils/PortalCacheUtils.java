package com.qunhe.its.networkportal.user.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PortalCacheUtils {

    private static final Cache<String, Integer> verificationCodeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    private static final Cache<String, Object> verificationSuccessCache = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.DAYS)
            .build();

    public static int generateAndCacheVerificationCode(String phoneNumber) {
        int verificationCode = generateRandomCode();
        verificationCodeCache.put(phoneNumber, verificationCode);
        return verificationCode;
    }

    public static void verificationSuccessCache(String phoneNumber) {
        verificationSuccessCache.put(phoneNumber, "SUCCESS");
    }

    public static Boolean getVerificationMobileSuccess(String phoneNumber) {
        return verificationSuccessCache.getIfPresent(phoneNumber) == "SUCCESS";
    }

    public static Integer getVerificationCode(String phoneNumber) {
        return verificationCodeCache.getIfPresent(phoneNumber);
    }

    private static int generateRandomCode() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // 生成4位数的验证码
    }


    private static final Cache<String, Object> verificationWxworkSuccessCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();

    public static void putCacheWxWork(String uuid) {
        verificationWxworkSuccessCache.put(uuid, "SUCCESS");
    }

    public static void putCacheWxWorkUnconfirmed(String uuid) {
        verificationWxworkSuccessCache.put(uuid, "UNCONFIRMED");
    }

    public static void delCacheWxWork(String uuid) {
        verificationWxworkSuccessCache.invalidate(uuid);
    }

    public static Object getCacheWxWork(String uuid) {
        return verificationWxworkSuccessCache.getIfPresent(uuid);
    }
}
