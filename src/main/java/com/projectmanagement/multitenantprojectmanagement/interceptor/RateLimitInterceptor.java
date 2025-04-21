package com.projectmanagement.multitenantprojectmanagement.interceptor;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.projectmanagement.multitenantprojectmanagement.config.CacheService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final CacheService cacheService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) {
        String key = "rate_limit:" + request.getRemoteAddr();
        int maxRequests = 10;
        int timeWindow = 60;

        if(!cacheService.isAllowed(key, maxRequests, timeWindow)) {
            response.setStatus(429);;
            response.setContentType("application/json");
            try {
                response.getWriter().write("Rate limit exceeded. Try again later");
            }catch(IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;

    }

}
