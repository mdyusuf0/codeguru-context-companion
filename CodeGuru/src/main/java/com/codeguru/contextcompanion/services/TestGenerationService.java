package com.codeguru.contextcompanion.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "features.test-generation.enabled", havingValue = "true")
public class TestGenerationService {
    // To be implemented in Task 16
}
