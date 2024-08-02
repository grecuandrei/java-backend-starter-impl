package com.store.application.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GeneralHealthIndicator implements HealthIndicator {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            if (!isDatabaseHealthy() || !isTableHealthy() || !isExternalServiceHealthy()) {
                return Health.down().withDetail("Store", "Something is wrong").build();
            }
            return Health.up().withDetail("Store", "All is well").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("Store", "Error").build();
        }
    }

    private boolean isDatabaseHealthy() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTableHealthy() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isExternalServiceHealthy() {
        try {
            String url = "http://external-service-url/api/health";
            String response = restTemplate.getForObject(url, String.class);
            return response != null && response.contains("UP");
        } catch (Exception e) {
            return false;
        }
    }
}
