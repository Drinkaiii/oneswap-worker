package com.oneswap.repositiry.impl;

import com.oneswap.model.Token;
import com.oneswap.repositiry.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpi implements TokenRepository {

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Token findAddressById(long id) {
        String sql = "SELECT * FROM token WHERE id = :id;";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<Token> tokens = namedParameterJdbcTemplate.query(sql, parameters, (RowMapper<Token>) (rs, rowNum) -> {
            Token token = new Token();
            token.setId(rs.getLong("id"));
            token.setName(rs.getString("name"));
            token.setSymbol(rs.getString("symbol"));
            token.setAddress(rs.getString("address"));
            token.setDecimals(rs.getInt("decimals"));
            token.setBlockchain(rs.getString("blockchain"));
            return token;
        });
        return (tokens.size() > 0) ? tokens.get(0) : null;
    }

}
