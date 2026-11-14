package com.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobPortalApplication {
    public static void main(String[] args) {
        // Use UTC everywhere. Some Windows machines report the legacy zone "Asia/Calcutta",
        // which the Postgres JDBC driver sends on connect and Postgres 16 rejects.
        // Must run before Spring creates the database connection.
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        SpringApplication.run(JobPortalApplication.class, args);
    }
}
