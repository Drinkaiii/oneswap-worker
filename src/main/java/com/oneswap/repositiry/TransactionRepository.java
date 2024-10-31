package com.oneswap.repositiry;

import com.oneswap.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository {

    List<Transaction> findByUserId(long userId);

}
