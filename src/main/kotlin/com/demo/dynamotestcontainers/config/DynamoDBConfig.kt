package com.demo.dynamotestcontainers.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dynamodb")
data class DynamoDBConfig(
    val region: String,
    val endpoint: String,
    val table: String
)