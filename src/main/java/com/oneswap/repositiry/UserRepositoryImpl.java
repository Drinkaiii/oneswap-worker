package com.oneswap.repositiry;

import com.oneswap.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User findUserByAddress(String address) {
        String sql = "SELECT * FROM user WHERE address = :address;";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("address", address);
        List<User> users = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<User>) (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setAddress(rs.getString("address"));
            return user;
        });
        return (users.size() > 0) ? users.get(0) : null;
    }
}
