package com.oneswap.service;

import com.oneswap.websocket.WebsocketPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Log4j2
public class NetworkService {

    private final WebsocketPushService websocketPushService;

    public void sendGas(String network, BigInteger gasPrice) {

        websocketPushService.sendGasUpdate(network, gasPrice);

    }

}
