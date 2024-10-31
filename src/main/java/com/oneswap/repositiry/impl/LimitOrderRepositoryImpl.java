package com.oneswap.repositiry.impl;

import com.oneswap.model.LimitOrder;
import com.oneswap.repositiry.LimitOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LimitOrderRepositoryImpl implements LimitOrderRepository {

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<LimitOrder> findByOrderId(long orderId) {
        String sql = "SELECT * FROM limit_order WHERE order_id = :orderId;";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("orderId", orderId);
        List<LimitOrder> limitOrders = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<LimitOrder>) (rs, rowNum) -> {
            LimitOrder limitOrder = new LimitOrder();
            limitOrder.setId(rs.getLong("id"));
            limitOrder.setStatus(rs.getString("status"));
            limitOrder.setOrderId(rs.getLong("order_id"));
            limitOrder.setUserId(rs.getLong("user_id"));
            limitOrder.setTokenInId(rs.getLong("token_in"));
            limitOrder.setTokenOutId(rs.getLong("token_out"));
            limitOrder.setAmountIn(rs.getBigDecimal("amount_in").toBigInteger());
            limitOrder.setMinAmountOut(rs.getBigDecimal("min_amount_out").toBigInteger());
            limitOrder.setFinalAmountOut(rs.getBigDecimal("final_amount_out").toBigInteger());

            return limitOrder;
        });
        return (limitOrders.size() > 0) ? limitOrders : null;
    }

    @Override
    public List<LimitOrder> findByUserId(long userId) {
        String sql = "SELECT * FROM limit_order WHERE user_id = :userId;";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        List<LimitOrder> limitOrders = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<LimitOrder>) (rs, rowNum) -> {
            LimitOrder limitOrder = new LimitOrder();
            limitOrder.setId(rs.getLong("id"));
            limitOrder.setStatus(rs.getString("status"));
            limitOrder.setOrderId(rs.getLong("order_id"));
            limitOrder.setUserId(rs.getLong("user_id"));
            limitOrder.setTokenInId(rs.getLong("token_in"));
            limitOrder.setTokenOutId(rs.getLong("token_out"));
            limitOrder.setAmountIn(rs.getBigDecimal("amount_in").toBigInteger());
            limitOrder.setMinAmountOut(rs.getBigDecimal("min_amount_out").toBigInteger());
            limitOrder.setFinalAmountOut(rs.getBigDecimal("final_amount_out").toBigInteger());

            return limitOrder;
        });
        return (limitOrders.size() > 0) ? limitOrders : null;
    }

}
