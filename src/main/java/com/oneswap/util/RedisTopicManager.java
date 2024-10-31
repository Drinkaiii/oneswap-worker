package com.oneswap.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisTopicManager {

    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter listenerAdapter;

    // dynamic subscribe specific topic
    public void subscribeToTopic(String topicName) {
        ChannelTopic topic = new ChannelTopic(topicName);
        container.addMessageListener(listenerAdapter, topic);
        log.info("Subscribed to Redis topic: " + topicName);
    }

    // dynamic unsubscribe specific topic
    public void unsubscribeFromTopic(String topicName) { //todo
        ChannelTopic topic = new ChannelTopic(topicName);
        container.removeMessageListener(listenerAdapter, topic);
        log.info("Unsubscribed from Redis topic: " + topicName);
    }
}

