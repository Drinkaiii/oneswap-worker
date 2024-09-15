package com.oneswap.service;

import com.oneswap.config.Network;
import com.oneswap.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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

    public List getAccountByAddress(String userAddress){
        Map<String,Object> request = new HashMap<>();
        request.put("id", 1);
        request.put("jsonrpc", "2.0");
        request.put("method", "alchemy_getTokenBalances");
        request.put("params", List.of(userAddress,"erc20"));
        ResponseEntity<Map> response = restTemplate.postForEntity(network.getNetworkRestUrl(), request, Map.class);

        // parse response
        List tokenInDecimalList = new ArrayList();
        List tokenList = (List)((Map) response.getBody().get("result")).get("tokenBalances");
        for (Object token : tokenList) {
            String tokenAddress = (String)((Map)token).get("contractAddress");
            String rawBalance = (String)((Map)token).get("tokenBalance");
            int decimals = tokenUtil.getTokenDecimalsByAddress(tokenAddress);
            BigDecimal balanceInDecimal = TokenUtil.convertHexToDecimal(rawBalance, decimals);
            tokenInDecimalList.add(Map.of("tokenAddress",tokenAddress,"balance",balanceInDecimal));
        }
        return tokenInDecimalList;
    }



}
