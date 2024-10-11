package com.oneswap.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneswap.service.LiquidityService;
import com.oneswap.service.NetworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisSubscriber implements MessageListener {

    private final LiquidityService liquidityService;
    private final NetworkService networkService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {

            String messageBody = new String(message.getBody()).trim();
            Map<String, String> data = objectMapper.readValue(messageBody, Map.class);
            String messageType = data.get("type");
            String messageData = data.get("data");

            switch (messageType) {
                case "gas":
                    handleGasMessage(new BigInteger(messageData));
                    break;
                case "liquidity":
                    String[] tokens = messageData.split(":");
                    handleLiquidityMessage(tokens[0], tokens[1]);
                    break;
                default:
                    log.warn("Unknown message type: " + messageType);
            }
        } catch (Exception e) {
            log.warn("Error processing Redis message: " + e.getMessage());
        }
    }

    // make thread async to send gas WebSocket message
    @Async
    public void handleGasMessage(BigInteger gasPrice) {
        networkService.sendGas("Sepolia", gasPrice); //todo
    }

    // make thread async to send liquidity WebSocket message
    @Async
    public void handleLiquidityMessage(String token0, String token1) {
        log.info("Processing liquidity for token pair: " + token0 + ":" + token1);
        liquidityService.updateAndSendEstimate(token0, token1);
    }

}
