package com.oneswap.websocket;

import com.oneswap.dto.AccountDto;
import com.oneswap.dto.EstimateRequest;
import com.oneswap.dto.MessageDto;
import com.oneswap.service.LiquidityService;
import com.oneswap.util.SessionManager;
import com.oneswap.util.RedisTopicManager;
import com.oneswap.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebsocketServer {

    private final LiquidityService liquidityService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TokenUtil tokenUtil;
    private final RedisTopicManager redisTopicManager;

    @MessageMapping("/account")
    @SendTo("/topic/account")
    public MessageDto<String> processAccountMessage(MessageDto<AccountDto> messageDto) {
        if ("account".equals(messageDto.getType())) {
            AccountDto account = messageDto.getData(); //TODO
            return new MessageDto<String>("account", "success", "account");
        }
        return new MessageDto<>("account", "failed", null);
    }

    @MessageMapping("/estimate")
    public void processEstimateMessage(EstimateRequest estimateRequest, SimpMessageHeaderAccessor headerAccessor) {
        // get the user's sessionId by SimpMessageHeaderAccessor
        String sessionId = headerAccessor.getSessionId();
        System.out.println("Session ID: " + sessionId);

        // save the user's request data
        SessionManager.addSession(sessionId, estimateRequest);

        // calculate amountOut
        String tokenPair = tokenUtil.getTokenPair(estimateRequest.getTokenIn(), estimateRequest.getTokenOut());
        redisTopicManager.subscribeToTopic(tokenPair);
        List result = liquidityService.findAllPathsInDecrease(estimateRequest.getTokenIn(), estimateRequest.getTokenOut(), estimateRequest.getAmountIn());

        // send result to the user
        messagingTemplate.convertAndSend("/queue/estimate/" + sessionId,
                new MessageDto<>("estimate", "success", result));
    }

    // listen disconnection events
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // get sessionId by SimpMessageHeaderAccessor
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        // remove the sessionId's session
        SessionManager.removeSession(sessionId);
        System.out.println("WebSocket disconnected: " + sessionId);
    }

}
