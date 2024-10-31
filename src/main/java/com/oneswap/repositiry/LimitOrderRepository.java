package com.oneswap.repositiry;

import com.oneswap.model.LimitOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimitOrderRepository {

    List<LimitOrder> findByOrderId(long orderId);

    List<LimitOrder> findByUserId(long userId);

}
