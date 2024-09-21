package com.oneswap.service;

import com.oneswap.dto.EstimateDto;
import com.oneswap.dto.EstimateRequest;
import com.oneswap.model.Liquidity;
import com.oneswap.util.RedisUtil;
import com.oneswap.util.SessionManager;
import com.oneswap.util.TokenUtil;
import com.oneswap.websocket.WebsocketPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class LiquidityService {

    @Value("${ONESWAP_FEE}")
    private double ONESWAP_FEE;

    private final RedisTemplate redisTemplate;
    private final RedisUtil redisUtil;
    private final TokenUtil tokenUtil;
    private final WebsocketPushService websocketPushService;

    public void updateAndSendEstimate(String tokenA, String tokenB) {
        // get users who subscribe specific token info and get they request amountIn
        Map<String, EstimateRequest> subscribers = SessionManager.getSubscribers(tokenA, tokenB);
        if (subscribers.isEmpty()) {
            // if no users subscribe the info, return
            System.out.println("No subscribers for token pair: " + tokenA + ":" + tokenB);
            return;
        }
        // according to every subscriber's amountIn to calculate amountOut
        for (Map.Entry<String, EstimateRequest> entry : subscribers.entrySet()) {
            String sessionId = entry.getKey();
            EstimateRequest estimateRequest = entry.getValue();
            BigInteger amountIn = estimateRequest.getAmountIn();
            List<Liquidity> result = findAllPathsInDecrease(estimateRequest.getTokenIn(), estimateRequest.getTokenOut(), amountIn);

            // push result to subscriber by WebSocket
            websocketPushService.sendLiquidityUpdate(sessionId, result);
        }
    }

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
            System.out.println(liquidity.getExchanger() + "：" + resultAmount);
        }
        return estimateDto;
    }

    public List findAllPathsInDecrease(String tokenIn, String tokenOut, BigInteger amountIn) {
        //List<String> tokens = tokenUtil.getTokenArray()
        tokenIn = tokenIn.toLowerCase();
        tokenOut = tokenOut.toLowerCase();
        // search match pool candidate
        List<String> keys = searchByTokens(tokenIn, tokenOut);
        List<Liquidity> liquidities = new ArrayList<>();
        for (String key : keys) {
            Liquidity liquidity = redisUtil.get(key, Liquidity.class);
            if (liquidity != null) {
                liquidity.initializeDecimals(tokenUtil);
                liquidities.add(liquidity);
            }
        }
        // prepare a list to store multiple EstimateDto results
        List<EstimateDto> estimateDtoList = new ArrayList<>();
        for (Liquidity liquidity : liquidities) {
            // process data input correctly
            BigInteger reserveIn, reserveOut;
            if (tokenIn.equalsIgnoreCase(liquidity.getToken0())) {
                reserveIn = liquidity.getAmount0();
                reserveOut = liquidity.getAmount1();
            } else if (tokenIn.equalsIgnoreCase(liquidity.getToken1())) {
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

            // create and add the EstimateDto to the list
            EstimateDto estimateDto = new EstimateDto();
            estimateDto.setAmountOut(resultAmount);
            estimateDto.setLiquidity(liquidity);
            estimateDto.setSlippage(0.01); //todo: set appropriate slippage value
            estimateDtoList.add(estimateDto);
        }

        // sort the list by resultAmount in descending order
        estimateDtoList.sort(Comparator.comparing(EstimateDto::getAmountOut).reversed());

        // return the sorted list
        return estimateDtoList;
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

        // Oneswap fee 0.2%
        double feeMultiplier = (100.0 - ONESWAP_FEE) / 100.0;

        // Uniswap fee 0.3%
        BigInteger amountInAfterYourFee = new BigDecimal(amountIn).multiply(BigDecimal.valueOf(feeMultiplier)).toBigInteger();
        BigInteger amountInWithUniswapFee = amountInAfterYourFee.multiply(BigInteger.valueOf(997));

        // calculate
        BigInteger numerator = amountInWithUniswapFee.multiply(reserveOut);
        BigInteger denominator = reserveIn.multiply(BigInteger.valueOf(1000)).add(amountInWithUniswapFee);

        // return value
        return numerator.divide(denominator);
    }


}
