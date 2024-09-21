package com.oneswap.util;

import com.oneswap.config.Network;
import com.oneswap.config.RestTemplateConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class TokenUtil {

    private final RestTemplate restTemplate;
    private final Network network;

    @Cacheable(value = "off-prefix-at-properties", key = "'token:decimals:' + #tokenAddress")
    public int getTokenDecimalsByAddress(String tokenAddress) {
        Map<String, Object> request = new HashMap<>();
        request.put("id", 1);
        request.put("jsonrpc", "2.0");
        request.put("method", "alchemy_getTokenMetadata");
        request.put("params", List.of(tokenAddress));
        ResponseEntity<Map> response = restTemplate.postForEntity(network.getNetworkRestUrl(), request, Map.class);
        Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");
        // check decimals is not null
        if (result == null || result.get("decimals") == null) {
            log.warn("Decimals information not available for token: " + tokenAddress);
            return 0; //todo
        }
        return (int) result.get("decimals");
    }

    // convert hex String to decimal BigInteger
    public static BigDecimal convertHexToDecimal(String hexBalance, int decimals) {
        try {
            // remove "0x" prefix , convert hex String to decimal BigInteger
            BigInteger balanceBigInt = new BigInteger(hexBalance.substring(2), 16);

            // calculate by decimals
            BigDecimal divisor = BigDecimal.TEN.pow(decimals);

            // return value in decimal
            return new BigDecimal(balanceBigInt).divide(divisor);
        } catch (Exception e) {
            throw new RuntimeException("parse token number wrong.", e);
        }
    }

    public String getTokenPair(String tokenA, String tokenB) {

        String token0, token1;
        if (tokenA.compareTo(tokenB) < 0) {
            token0 = tokenA.toLowerCase();
            token1 = tokenB.toLowerCase();
        } else {
            token0 = tokenB.toLowerCase();
            token1 = tokenA.toLowerCase();
        }

        // combine two token address as pair
        String pair = token0 + ":" + token1;
        return pair;
    }

    public List<String> getTokenArray(String tokenA, String tokenB) {

        String token0, token1;
        if (tokenA.compareTo(tokenB) < 0) {
            token0 = tokenA.toLowerCase();
            token1 = tokenB.toLowerCase();
        } else {
            token0 = tokenB.toLowerCase();
            token1 = tokenA.toLowerCase();
        }

        // combine two token address as pair
        List result = List.of(token0, token1);
        return result;
    }
}
