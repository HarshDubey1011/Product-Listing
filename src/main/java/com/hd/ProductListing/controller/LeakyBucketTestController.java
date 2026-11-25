package com.hd.ProductListing.controller;

import com.hd.ProductListing.service.LeakyBucketRateLimiterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test-rate-limiter")
public class LeakyBucketTestController {
    private final LeakyBucketRateLimiterService rateLimiterService;
    public LeakyBucketTestController(LeakyBucketRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping
    public String testRateLimiter(@RequestParam(defaultValue = "test-client") String client) {
        boolean allowed = rateLimiterService.allowRequest(client);

        return allowed ? "REQUEST allowed" : "REQUESTED rejected";
    }
}
