package com.oneswap.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    private long id;
    private String name;
    private String symbol;
    private String address;
    private int decimals;
    private String blockchain;

}

