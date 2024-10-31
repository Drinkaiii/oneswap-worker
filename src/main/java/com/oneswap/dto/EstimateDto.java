package com.oneswap.dto;

import com.oneswap.model.Liquidity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateDto {

    private BigInteger amountOut;
    private double slippage;
    private Liquidity liquidity;

}
