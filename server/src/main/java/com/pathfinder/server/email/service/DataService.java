package com.pathfinder.server.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class DataService {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataService(javax.sql.DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void saveValues(String authKey, String authCode, Duration duration) {
        // 데이터베이스에 인증 코드를 저장하는 로직을 구현
        String sql = "INSERT INTO auth_data (auth_key, auth_code, expiration_time) VALUES (?, ?, ?)";
        LocalDateTime expirationTime = LocalDateTime.now().plus(duration);
        jdbcTemplate.update(sql, authKey, authCode, expirationTime);
    }

    public String getValues(String authKey) {
        // 데이터베이스에서 인증 코드를 가져오는 로직을 구현
        String sql = "SELECT auth_code FROM auth_data WHERE auth_key = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, authKey);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void deleteValues(String authKey) {
        // 데이터베이스에서 인증 코드를 삭제하는 로직을 구현
        String sql = "DELETE FROM auth_data WHERE auth_key = ?";
        jdbcTemplate.update(sql, authKey);
    }
}
