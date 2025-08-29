package com.osc.oscms.materialservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Material Service Security Configuration
 * 配置资料服务的安全策略
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF (对于微服务API通常不需要)
                .csrf(csrf -> csrf.disable())

                // 配置会话管理为无状态 (微服务架构)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许访问健康检查端点
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // 允许访问API文档
                        .requestMatchers("/doc.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 允许访问所有课程资源接口 (临时配置，后续可以添加JWT验证)
                        .requestMatchers("/courses/**", "/materials/**", "/files/**").permitAll()

                        // 其他请求需要认证
                        .anyRequest().authenticated());

        return http.build();
    }
}

