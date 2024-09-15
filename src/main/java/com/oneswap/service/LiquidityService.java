package com.oneswap.service;

import com.oneswap.dto.EstimateDto;
import com.oneswap.model.Liquidity;
import com.oneswap.util.RedisUtil;
import com.oneswap.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class LiquidityService {

    private final RedisTemplate redisTemplate;
    private final RedisUtil redisUtil;
    private final TokenUtil tokenUtil;

    public EstimateDto findTheBestPath(String firstToken, String secondToken, BigInteger amountIn) {
        // search match pool candidate
        List<String> keys = searchByTokens(firstToken, secondToken);
        List<Liquidity> liquidities = new ArrayList<>();
        for (String key : keys) {
            Liquidity liquidity = redisUtil.get(key, Liquidity.class);
            if (liquidity != null) {
                liquidity.initializeDecimals(tokenUtil);
                liquidities.add(liquidity);
            }
        }
        // find the best transaction path
        EstimateDto estimateDto = new EstimateDto();
        BigInteger maxAmount = BigInteger.ZERO;
        for (Liquidity liquidity : liquidities) {
            // process data input correctly
            BigInteger reserveIn, reserveOut;
            if (firstToken.equalsIgnoreCase(liquidity.getToken0())) {
                reserveIn = liquidity.getAmount0();
                reserveOut = liquidity.getAmount1();
            } else if (firstToken.equalsIgnoreCase(liquidity.getToken1())) {
                reserveIn = liquidity.getAmount1();
                reserveOut = liquidity.getAmount0();
            } else {
                // skip mismatch pool
                continue;
            }

            // calculate amountOut
            BigInteger resultAmount = calculateAmount(reserveIn, reserveOut, amountIn);
            // check liquidity enough
            if (resultAmount.compareTo(reserveOut) > 0) {
                log.warn("skip the pool: The reserve amount is greater than the reserve amount");
                // skip the pool
                continue;
            }
            // prepare return value
            if (resultAmount.compareTo(maxAmount) > 0) {
                maxAmount = resultAmount;
                estimateDto.setAmountOut(maxAmount);
                estimateDto.setLiquidity(liquidity);
                estimateDto.setSlippage(0.01); //todo
            }
        }
        return estimateDto;
    }

    private List<String> searchByTokens(String firstToken, String secondToken) {
        // the first filter
        List<String> firstSearchResults = searchByFirstToken(firstToken);
        // the second filter and return
        return searchBySecondToken(firstSearchResults, secondToken);
    }

    private List<String> searchByFirstToken(String firstToken) {
        Set<String> keys = redisTemplate.keys("*" + firstToken + "*");
        return keys != null ? new ArrayList<>(keys) : new ArrayList<>();
    }

    private List<String> searchBySecondToken(List<String> firstSearchResults, String secondToken) {
        List<String> filteredResults = new ArrayList<>();
        for (String key : firstSearchResults) {
            if (key.contains(secondToken))
                filteredResults.add(key);
        }
        return filteredResults;
    }

    private BigInteger calculateAmount(BigInteger reserveIn, BigInteger reserveOut, BigInteger amountIn) {
        // (reserveOut * amountIn) / (reserveIn + amountIn)
        return reserveOut.multiply(amountIn).divide(reserveIn.add(amountIn));
    }

}
