package com.oneswap.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    private long id;
    private long userId; // different from core back-end setting
    private String transactionHash;
    private String blockchain;
    private int exchanger;
    private long tokenInId; // different from core back-end setting
    private long tokenOutId; // different from core back-end setting
    private BigInteger amountIn;
    private BigInteger amountOut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
    private Token tokenIn;
    private Token tokenOut;

}
