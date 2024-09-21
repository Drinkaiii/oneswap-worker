package com.oneswap.websocket;

import com.oneswap.dto.MessageDto;
import com.oneswap.model.Liquidity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebsocketPushService {

    private final SimpMessagingTemplate messagingTemplate;

    // 發送流動性更新給特定的訂閱者
    public void sendLiquidityUpdate(String sessionId, List<Liquidity> liquidities) {
        messagingTemplate.convertAndSend("/queue/estimate/" + sessionId,
                new MessageDto<>("estimate", "success", liquidities));
    }

}
