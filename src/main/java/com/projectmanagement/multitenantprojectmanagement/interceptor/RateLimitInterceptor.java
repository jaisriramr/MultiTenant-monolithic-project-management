package com.projectmanagement.multitenantprojectmanagement.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    // private final CacheService cacheService;

    // @Override
    // public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) {
    //     String key = "rate_limit:" + request.getRemoteAddr();
    //     int maxRequests = 1000;
    //     int timeWindow = 60;

    //     if(!cacheService.isAllowed(key, maxRequests, timeWindow)) {
    //         response.setStatus(429);;
    //         response.setContentType("application/json");
    //         try {
    //             response.getWriter().write("Rate limit exceeded. Try again later");
    //         }catch(IOException e) {
    //             e.printStackTrace();
    //         }
    //         return false;
    //     }
    //     return true;

    // }

}
