package com.oneswap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LiquiditySubscriber implements MessageListener {

    private final LiquidityService liquidityService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Received Redis message: " + new String(message.getBody()));
        String tokenPair = (new String(message.getBody())).trim().replaceAll("^\"|\"$", "");
        String[] parts = tokenPair.split(":");
        String token0 = parts[0];
        String token1 = parts[1];

        sendWebSocketMessage(token0, token1);
    }

    // make thread async to send WebSocket message
    @Async
    public <T> void sendWebSocketMessage(String token0, String token1) {
        liquidityService.updateAndSendEstimate(token0, token1);
    }

}
