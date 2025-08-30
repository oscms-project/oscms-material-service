package com.osc.oscms.materialservice.config;

import com.osc.oscms.materialservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Material Service Security Configuration
 * 配置资料服务的安全策略
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http
                // 禁用 CSRF (对于微服务API通常不需要)
                .csrf(csrf -> csrf.disable())

                // 配置会话管理为无状态 (微服务架构)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许访问健康检查端点
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // 允许访问API文档
                        .requestMatchers("/doc.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 课程资源接口需要认证
                        .requestMatchers("/courses/**", "/materials/**", "/files/**").authenticated()

                        // 其他请求需要认证
                        .anyRequest().authenticated());

        return http.build();
    }
}
