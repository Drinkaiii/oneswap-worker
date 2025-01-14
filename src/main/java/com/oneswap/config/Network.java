package com.oneswap.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Network {

    private String NetworkRestUrl;
    private String NetworkWebSocketUrl;

}
