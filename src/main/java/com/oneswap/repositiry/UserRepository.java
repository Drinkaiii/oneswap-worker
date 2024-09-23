package com.oneswap.repositiry;

import com.oneswap.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    User findUserByAddress(String address);

}
