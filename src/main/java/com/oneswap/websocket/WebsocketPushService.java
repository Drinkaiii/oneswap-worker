package com.oneswap.websocket;

import com.oneswap.dto.MessageDto;
import com.oneswap.model.Liquidity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebsocketPushService {

    private final SimpMessagingTemplate messagingTemplate;

    // send liquidity update data
    public void sendLiquidityUpdate(String sessionId, List<Liquidity> liquidities) {
        messagingTemplate.convertAndSend("/queue/estimate/" + sessionId,
                new MessageDto<>("estimate", "success", liquidities));
    }

    // send gas update data
    public void sendGasUpdate(String network, BigInteger gasPrice) {
        messagingTemplate.convertAndSend("/queue/gas/" + network,
                new MessageDto<>("gas", "success", gasPrice));
    }

}
