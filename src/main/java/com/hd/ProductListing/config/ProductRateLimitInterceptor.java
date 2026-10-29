
package com.hd.ProductListing.config;

import com.hd.ProductListing.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProductRateLimitInterceptor
        implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {

        String clientIp = request.getRemoteAddr();

        boolean allowed =
                rateLimiterService.allowRequest(clientIp);

        if (allowed) {
            return true;
        }

        response.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value()
        );
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                  "status": 429,
                  "error": "Too Many Requests",
                  "message": "Rate limit exceeded. Try again later."
                }
                """);

        return false;
    }
}
