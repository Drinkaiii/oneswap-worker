package com.oneswap.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LimitOrder {

    public static String STATUS_UN_FILLED = "unfilled";
    public static String STATUS_FILLED = "filled";
    public static String STATUS_CANCELED = "canceled";
    public static String STATUS_ERROR = "error";

    private long id;
    private String status;
    private long orderId;
    private long userId;
    private long tokenInId;
    private long tokenOutId;
    private BigInteger amountIn;
    private BigInteger minAmountOut;
    private BigInteger finalAmountOut;

    private User user;
    private Token tokenIn;
    private Token tokenOut;

}
