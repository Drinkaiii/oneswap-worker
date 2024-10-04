package com.oneswap.config;

import com.oneswap.util.RedisTopicManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner {

    @Value("${blockchain}")
    private String blockchain;
    private final RedisTopicManager redisTopicManager;

    @PostConstruct
    public void subscribeToRedisTopic() {
        redisTopicManager.subscribeToTopic("gas:" + blockchain);
    }

}
