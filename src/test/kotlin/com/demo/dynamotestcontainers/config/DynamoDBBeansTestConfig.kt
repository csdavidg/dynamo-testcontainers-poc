package com.demo.dynamotestcontainers.config

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.http.Url
import com.demo.dynamotestcontainers.services.CarServiceTest
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.testcontainers.containers.localstack.LocalStackContainer

@TestConfiguration
class DynamoDBBeansTestConfig(private val dynamoDBConfig: DynamoDBConfig) {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun dynamoDBClient() = DynamoDbClient {
        region = CarServiceTest.localStack.region
        endpointUrl =
            Url.parse(CarServiceTest.localStack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toURL().toString())
        credentialsProvider = StaticCredentialsProvider(Credentials(CarServiceTest.localStack.accessKey, CarServiceTest.localStack.secretKey))
    }
}