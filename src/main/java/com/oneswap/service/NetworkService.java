package com.oneswap.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public interface NetworkService {

    void sendGas(String network, BigInteger gasPrice);

}
