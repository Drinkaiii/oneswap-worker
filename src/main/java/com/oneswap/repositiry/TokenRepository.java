package com.oneswap.repositiry;

import com.oneswap.model.Token;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository {

    Token findAddressById(long id);

}
