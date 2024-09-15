package com.oneswap.util;

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
public class TokenUtil {

    @Value("${ALCHEMY_ETHEREUM_REST_URL}")
    private String ALCHEMY_ETHEREUM_REST_URL;

    @Cacheable(value = "off-prefix-at-properties", key = "'token:decimals:' + #tokenAddress")
    public int getTokenDecimalsByAddress(String tokenAddress) {
        Map<String,Object> request = new HashMap<>();
        request.put("id", 1);
        request.put("jsonrpc", "2.0");
        request.put("method", "alchemy_getTokenMetadata");
        request.put("params", List.of(tokenAddress));
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(ALCHEMY_ETHEREUM_REST_URL, request, Map.class);
        return (int) ((Map) response.getBody().get("result")).get("decimals");
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

}
