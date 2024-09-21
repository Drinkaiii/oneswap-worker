package com.oneswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateRequest {

    private String address;
    private String tokenIn;
    private String tokenOut;
    private BigInteger amountIn;
}
