package com.qunhe.its.networkportal.user.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yantao
 * @date 13.12.23 11:29
 */
public class PropertiesUtils {

    public static Properties properties;

    static {
        Properties properties = new Properties();
        try (InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PropertiesUtils.properties = properties;
    }
}
