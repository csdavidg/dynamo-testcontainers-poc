package com.demo.dynamotestcontainers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
class DynamoTestcontainersApplication

fun main(args: Array<String>) {
	runApplication<DynamoTestcontainersApplication>(*args)
}
