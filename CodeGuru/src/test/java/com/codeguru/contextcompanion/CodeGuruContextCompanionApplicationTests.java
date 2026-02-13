package com.codeguru.contextcompanion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "aws.region=us-east-1",
    "features.translation.enabled=false",
    "features.test-generation.enabled=false"
})
class CodeGuruContextCompanionApplicationTests {

    @Test
    void contextLoads() {
        // Verify that the Spring context loads successfully
    }
}
