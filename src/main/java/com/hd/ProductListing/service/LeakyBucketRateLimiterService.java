
package com.hd.ProductListing.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

@Service
public class LeakyBucketRateLimiterService {

    private static final int BUCKET_CAPACITY = 5;

    private static final double LEAK_RATE = 1.0;

    private static final long TTL_SECONDS = 6;

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<Long> leakyBucketScript;

    public LeakyBucketRateLimiterService(
            StringRedisTemplate redisTemplate
    ) throws IOException {

        this.redisTemplate = redisTemplate;

        String script = Files.readString(
                new ClassPathResource(
                        "scripts/leaky-bucket.lua"
                ).getFile().toPath(),
                StandardCharsets.UTF_8
        );

        this.leakyBucketScript = new DefaultRedisScript<>();
        this.leakyBucketScript.setScriptText(script);
        this.leakyBucketScript.setResultType(Long.class);
    }

    public boolean allowRequest(String clientIp) {

        long nowMillis = Instant.now().toEpochMilli();

        String key = "rate-limit:leaky:" + clientIp;

        Long result = redisTemplate.execute(
                leakyBucketScript,
                List.of(key),
                String.valueOf(BUCKET_CAPACITY),
                String.valueOf(LEAK_RATE),
                String.valueOf(nowMillis),
                String.valueOf(TTL_SECONDS)
        );

        if (result == null) {
            throw new IllegalStateException(
                    "Redis did not return a leaky bucket decision"
            );
        }

        return result == 1L;
    }
}
