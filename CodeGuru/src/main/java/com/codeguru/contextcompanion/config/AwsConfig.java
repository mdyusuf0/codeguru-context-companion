package com.codeguru.contextcompanion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.qbusiness.QBusinessClient;

import java.time.Duration;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.bedrock.timeout}")
    private int bedrockTimeout;

    @Value("${aws.q.timeout}")
    private int qTimeout;

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient() {
        return BedrockRuntimeClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(config -> config
                        .apiCallTimeout(Duration.ofMillis(bedrockTimeout))
                        .apiCallAttemptTimeout(Duration.ofMillis(bedrockTimeout))
                )
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "features.test-generation.enabled", havingValue = "true")
    public QBusinessClient qBusinessClient() {
        return QBusinessClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .overrideConfiguration(config -> config
                        .apiCallTimeout(Duration.ofMillis(qTimeout))
                        .apiCallAttemptTimeout(Duration.ofMillis(qTimeout))
                )
                .build();
    }
}
