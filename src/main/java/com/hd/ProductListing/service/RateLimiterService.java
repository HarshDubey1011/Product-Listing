
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
public class RateLimiterService {

    private static final int LIMIT = 5;
    private static final long WINDOW_SECONDS = 60;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> fixedWindowScript;

    public RateLimiterService(StringRedisTemplate redisTemplate)
            throws IOException {

        this.redisTemplate = redisTemplate;

        String script = Files.readString(
                new ClassPathResource(
                        "scripts/sliding-window.lua"
                ).getFile().toPath(),
                StandardCharsets.UTF_8
        );

        this.fixedWindowScript = new DefaultRedisScript<>();
        this.fixedWindowScript.setScriptText(script);
        this.fixedWindowScript.setResultType(Long.class);
    }

    public boolean allowRequest(String clientIp) {

        long epochSeconds = Instant.now().getEpochSecond();

        long windowId = epochSeconds / WINDOW_SECONDS;

        long elapsed = epochSeconds % WINDOW_SECONDS;

        long ttl = (2 * WINDOW_SECONDS) - elapsed;

        String currentKey = "rate-limit:products:"
                + clientIp + ":" + windowId;

        String previousKey = "rate-limit:products:"
                + clientIp + ":" + (windowId - 1);

        Long result = redisTemplate.execute(
                fixedWindowScript,
                List.of(currentKey, previousKey),
                String.valueOf(LIMIT),
                String.valueOf(WINDOW_SECONDS),
                String.valueOf(elapsed),
                String.valueOf(ttl)
        );

        if (result == null) {
            throw new IllegalStateException(
                    "Redis did not return a rate-limit decision"
            );
        }

        return result == 1L;
    }
}
