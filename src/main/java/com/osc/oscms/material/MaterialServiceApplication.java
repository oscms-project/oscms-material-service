package com.osc.oscms.material;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.openfeign.EnableFeignClients; // 将来需要时启用

@SpringBootApplication
@MapperScan("com.osc.oscms.material.mapper") // 指向 material-service 自己的 mapper 包
// @EnableFeignClients(basePackages = "com.osc.oscms.material.client") // 将来需要时启用
public class MaterialServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaterialServiceApplication.class, args);
    }
}