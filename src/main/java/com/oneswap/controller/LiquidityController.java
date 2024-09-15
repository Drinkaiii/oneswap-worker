package com.oneswap.controller;

import com.oneswap.dto.ErrorResponseDto;
import com.oneswap.dto.EstimateDto;
import com.oneswap.service.LiquidityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/1.0/liquidity")
public class LiquidityController {

    private final LiquidityService liquidityService;

    @GetMapping("/estimate")
    public ResponseEntity<?> getestimate(String tokenIn, String tokenOut, BigInteger amountIn) {
        EstimateDto estimateDto = liquidityService.findTheBestPath(tokenIn, tokenOut, amountIn);
        if (estimateDto.getAmountOut() != null) {
            return ResponseEntity.ok(estimateDto);
        } else {
            return new ResponseEntity<>(ErrorResponseDto.error("no liquidity"), HttpStatus.OK);
        }
    }

}
