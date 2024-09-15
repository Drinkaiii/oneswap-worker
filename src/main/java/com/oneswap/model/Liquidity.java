package com.oneswap.model;

import com.oneswap.util.TokenUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Liquidity {

    private String token0;
    private String token1;
    private BigInteger amount0;
    private BigInteger amount1;
    private int decimals0;
    private int decimals1;
    private String exchanger;
    private String algorithm;
    private double weight;

    public void initializeDecimals(TokenUtil tokenUtil) {
        this.decimals0 = tokenUtil.getTokenDecimalsByAddress(this.token0);
        this.decimals1 = tokenUtil.getTokenDecimalsByAddress(this.token1);
    }

}
