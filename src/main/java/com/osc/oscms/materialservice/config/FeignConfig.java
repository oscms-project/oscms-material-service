package com.osc.oscms.materialservice.config;

import com.osc.oscms.common.util.JwtTokenHolder;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                String token = JwtTokenHolder.getCurrentToken();
                if (token != null) {
                    template.header("Authorization", "Bearer " + token);
                }
            }
        };
    }
}


