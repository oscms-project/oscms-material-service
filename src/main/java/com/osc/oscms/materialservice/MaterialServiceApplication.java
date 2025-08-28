package com.osc.oscms.materialservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * OSCMS 资料服务主启动类
 * 负责管理教学资料和版本控制
 */
@SpringBootApplication(scanBasePackages = {
    "com.osc.oscms.materialservice",
    "com.osc.oscms.common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class MaterialServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaterialServiceApplication.class, args);
    }
}
