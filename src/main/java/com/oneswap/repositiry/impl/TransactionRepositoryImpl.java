package com.oneswap.repositiry.impl;

import com.oneswap.model.Transaction;
import com.oneswap.repositiry.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Transaction> findByUserId(long userId) {
        String sql = "SELECT * FROM transaction WHERE user_id = :userId;";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        List<Transaction> transactions = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<Transaction>) (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getLong("id"));
            transaction.setUserId(rs.getLong("user_id"));
            transaction.setTransactionHash(rs.getString("transaction_hash"));
            transaction.setBlockchain(rs.getString("blockchain"));
            transaction.setExchanger(rs.getInt("exchanger"));
            transaction.setTokenInId(rs.getLong("token_in"));
            transaction.setTokenOutId(rs.getLong("token_out"));
            transaction.setAmountIn(rs.getBigDecimal("amount_in").toBigInteger());
            transaction.setAmountOut(rs.getBigDecimal("amount_out").toBigInteger());
            transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            transaction.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

            return transaction;
        });
        return (transactions.size() > 0) ? transactions : null;
    }

}
