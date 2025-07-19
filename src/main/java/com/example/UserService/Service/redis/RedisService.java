package com.example.UserService.Service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void testConnectionOnStartup() {
        try {
            redisTemplate.hasKey("test-key");
            System.out.println("✅ Redis connection test SUCCESS");
        } catch (Exception e) {
            System.err.println("❌ Redis connection test FAILED");
            e.printStackTrace();
        }
    }

    public boolean sendApplicationMessage(Object message, String channel) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(channel, json);
            System.out.println("✅ Sent message to Redis channel '" + channel + "': " + json);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to send message to Redis channel '" + channel + "'");
            e.printStackTrace();
        }
        return false;
    }
}
