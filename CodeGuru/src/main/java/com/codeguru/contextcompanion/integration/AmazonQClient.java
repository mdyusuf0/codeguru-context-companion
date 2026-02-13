package com.codeguru.contextcompanion.integration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "features.test-generation.enabled", havingValue = "true")
public class AmazonQClient {
    // To be implemented in Task 3
}
