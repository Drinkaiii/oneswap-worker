package com.oneswap.service;

import com.oneswap.config.Network;
import com.oneswap.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final TokenUtil tokenUtil;
    private final RestTemplate restTemplate;
    private final Network network;

    public List<Map<String, Object>> getAccountByAddress(String userAddress) {
        Map<String, Object> request = new HashMap<>();
        request.put("id", 1);
        request.put("jsonrpc", "2.0");
        request.put("method", "alchemy_getTokenBalances");
        request.put("params", List.of(userAddress, "erc20"));
        ResponseEntity<Map> response = restTemplate.postForEntity(network.getNetworkRestUrl(), request, Map.class);

        // Parse response
        List<Map<String, Object>> tokenInDecimalList = new ArrayList<>();
        List tokenList = (List) ((Map) response.getBody().get("result")).get("tokenBalances");
        for (Object token : tokenList) {
            String tokenAddress = (String) ((Map) token).get("contractAddress");
            String rawBalance = (String) ((Map) token).get("tokenBalance");
            int decimals = tokenUtil.getTokenDecimalsByAddress(tokenAddress);

            // Return raw balance and decimals to front-end
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("tokenAddress", tokenAddress);
            tokenInfo.put("balance", rawBalance);
            tokenInfo.put("decimals", decimals);

            tokenInDecimalList.add(tokenInfo);
        }
        return tokenInDecimalList;
    }
}
