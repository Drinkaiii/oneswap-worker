package com.oneswap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NetworkConfig {

    @Value("${ALCHEMY_ETHEREUM_REST_URL}")
    private String ALCHEMY_ETHEREUM_REST_URL;

    @Value("${ALCHEMY_SEPOLIA_REST_URL}")
    private String ALCHEMY_SEPOLIA_REST_URL;

    @Value("${blockchain}")
    private String blockchain;

    @Bean
    public Network getNetwork() {
        Network network = new Network();
        switch (blockchain) {
            case "Ethereum":
                network.setNetworkRestUrl(ALCHEMY_ETHEREUM_REST_URL);
                break;
            case "Sepolia":
                network.setNetworkRestUrl(ALCHEMY_SEPOLIA_REST_URL);
                break;
            default:
                network.setNetworkRestUrl(ALCHEMY_ETHEREUM_REST_URL);
        }
        return network;
    }
}


