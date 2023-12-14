package com.qunhe.its.networkportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NetworkPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkPortalApplication.class, args);
    }

}
