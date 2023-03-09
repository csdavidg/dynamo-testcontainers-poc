package com.demo.dynamotestcontainers.config

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.smithy.kotlin.runtime.http.Url
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode

@Configuration
class DynamoDBBeansConfig(private val dynamoDBConfig: DynamoDBConfig) {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun dynamoDBClient() = DynamoDbClient {
        region = dynamoDBConfig.region
        endpointUrl = Url.parse(dynamoDBConfig.endpoint)
    }
}