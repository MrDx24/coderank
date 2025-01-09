package com.coderank.execution.ExecutionService.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseSecretsUtil {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSecretsUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, String> getSecrets(String secretName) {
        String sql = "SELECT secret_key, secret_value FROM secrets WHERE secret_name = ?";
        Map<String, String> secrets = new HashMap<>();

        jdbcTemplate.query(sql, new Object[]{secretName}, rs -> {
            secrets.put(rs.getString("secret_key"), rs.getString("secret_value"));
        });

        return secrets;
    }

}
