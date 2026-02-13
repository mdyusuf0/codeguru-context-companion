package com.codeguru.contextcompanion.services;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "features.translation.enabled", havingValue = "true")
public class TranslationService {
    // To be implemented in Task 15
}
