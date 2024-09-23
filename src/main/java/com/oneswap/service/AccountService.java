package com.oneswap.service;

import com.oneswap.config.Network;
import com.oneswap.dto.AccountDto;
import com.oneswap.model.LimitOrder;
import com.oneswap.model.Token;
import com.oneswap.model.Transaction;
import com.oneswap.model.User;
import com.oneswap.repositiry.LimitOrderRepository;
import com.oneswap.repositiry.TokenRepository;
import com.oneswap.repositiry.TransactionRepository;
import com.oneswap.repositiry.UserRepository;
import com.oneswap.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final TokenUtil tokenUtil;
    private final RestTemplate restTemplate;
    private final Network network;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final LimitOrderRepository limitOrderRepository;

    public List<AccountDto> getAccountByAddress(String userAddress) {
        Map<String, Object> request = new HashMap<>();
        request.put("id", 1);
        request.put("jsonrpc", "2.0");
        request.put("method", "alchemy_getTokenBalances");
        request.put("params", List.of(userAddress, "erc20"));
        ResponseEntity<Map> response = restTemplate.postForEntity(network.getNetworkRestUrl(), request, Map.class);

        // Parse response
        List<AccountDto> tokenInDecimalList = new ArrayList<>();
        List tokenList = (List) ((Map) response.getBody().get("result")).get("tokenBalances");
        for (Object token : tokenList) {
            String tokenAddress = (String) ((Map) token).get("contractAddress");
            String rawBalance = (String) ((Map) token).get("tokenBalance");
            int decimals = tokenUtil.getTokenDecimalsByAddress(tokenAddress);

            // Return raw balance and decimals to front-end
            AccountDto accountDto = AccountDto.builder()
                    .tokenAddress(tokenAddress)
                    .balance(rawBalance)
                    .decimals(decimals)
                    .build();
            tokenInDecimalList.add(accountDto);
        }
        return tokenInDecimalList;
    }

    public List<Transaction> getTransactionByAddress(String userAddress) {
        User user = userRepository.findUserByAddress(userAddress);
        if (user == null)
            return new ArrayList<>();
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());
        if (transactions == null)
            return transactions;
        for (Transaction transaction : transactions) {
            Token tokenIn = tokenRepository.findAddressById(transaction.getTokenInId());
            transaction.setTokenIn(tokenIn);
            Token tokenOut = tokenRepository.findAddressById(transaction.getTokenOutId());
            transaction.setTokenOut(tokenOut);
        }
        return transactions;
    }

    public List<LimitOrder> getLimitOrderByAddress(String userAddress){
        User user = userRepository.findUserByAddress(userAddress);
        if (user == null)
            return new ArrayList<>();
        List<LimitOrder> limitOrders = limitOrderRepository.findByUserId(user.getId());
        if (limitOrders == null)
            return limitOrders;
        for (LimitOrder limitOrder : limitOrders) {
            Token tokenIn = tokenRepository.findAddressById(limitOrder.getTokenInId());
            limitOrder.setTokenIn(tokenIn);
            Token tokenOut = tokenRepository.findAddressById(limitOrder.getTokenOutId());
            limitOrder.setTokenOut(tokenOut);
        }
        return limitOrders;
    }


}
