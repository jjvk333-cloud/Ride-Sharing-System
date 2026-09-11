package com.velto.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically verifies MongoDB connection and write/read capability on application startup.
 */
@Component
public class MongoConnectionVerifier implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoConnectionVerifier.class);
    private final MongoTemplate mongoTemplate;

    public MongoConnectionVerifier(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            String dbName = mongoTemplate.getDb().getName();
            log.info("Checking MongoDB connection to database: '{}'...", dbName);

            // Write a test document to confirm write permission
            Map<String, Object> testDoc = new HashMap<>();
            testDoc.put("service", "VELTO Backend");
            testDoc.put("status", "CONNECTED");
            testDoc.put("timestamp", new Date());

            mongoTemplate.save(testDoc, "system_health");

            // Read the document back to confirm read permission
            long count = mongoTemplate.getCollection("system_health").countDocuments();

            log.info("==================================================================");
            log.info("✅ VELTO MONGODB CONNECTION VERIFIED SUCCESSFULLY!");
            log.info("   Database Name : {}", dbName);
            log.info("   Collection    : system_health (Documents: {})", count);
            log.info("   Status        : READ & WRITE OPERATIONS FUNCTIONAL");
            log.info("==================================================================");
        } catch (Exception e) {
            log.error("❌ MONGODB CONNECTION FAILED: {}", e.getMessage(), e);
        }
    }
}
