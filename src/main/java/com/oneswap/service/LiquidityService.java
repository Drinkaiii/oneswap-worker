package com.oneswap.service;

import com.oneswap.dto.EstimateDto;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public interface LiquidityService {

    void updateAndSendEstimate(String tokenA, String tokenB);

    EstimateDto findTheBestPath(String firstToken, String secondToken, BigInteger amountIn);

    List findAllPathsInDecrease(String tokenIn, String tokenOut, BigInteger amountIn);

}
