package com.oneswap.service;

import com.oneswap.dto.AccountDto;
import com.oneswap.model.LimitOrder;
import com.oneswap.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

    List<AccountDto> getAccountByAddress(String userAddress);

    List<Transaction> getTransactionByAddress(String userAddress);

    List<LimitOrder> getLimitOrderByAddress(String userAddress);
}
