package com.demo.dynamotestcontainers.services

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import com.demo.dynamotestcontainers.config.DynamoDBConfig
import com.demo.dynamotestcontainers.data.Car
import com.demo.dynamotestcontainers.exceptions.CarNotFoundException
import org.springframework.stereotype.Service

private const val KEY_NAME = "name"

private fun GetItemResponse.toCar(): Car? =
    this.item?.let {
        Car(name = it[KEY_NAME]?.asS(), brand = it["brand"]?.asS(), model = it["model"]?.asS()?.toInt())
    }

private fun Car.toItemsMap(): MutableMap<String, AttributeValue> = mutableMapOf(
    KEY_NAME to AttributeValue.S(this.name ?: ""),
    "model" to AttributeValue.S(this.model.toString()),
    "brand" to AttributeValue.S(this.brand ?: "")
)

@Service
class CarService(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoDBConfig: DynamoDBConfig
) : ICarService {

    override suspend fun createCar(car: Car) {
        dynamoDbClient.use { dynamoClient ->
            dynamoClient.putItem(PutItemRequest {
                tableName = dynamoDBConfig.table
                item = car.toItemsMap()
            })
        }
    }

    override suspend fun getCarByName(name: String): Car {
        val request = GetItemRequest {
            key = mutableMapOf(KEY_NAME to AttributeValue.S(name))
            tableName = dynamoDBConfig.table
        }

        dynamoDbClient.use { dynamoClient ->
            return dynamoClient.getItem(request).toCar() ?: throw CarNotFoundException("The car was not found")
        }
    }

    override suspend fun deleteCar(name: String) {
        val request = DeleteItemRequest {
            tableName = dynamoDBConfig.table
            key = mutableMapOf(KEY_NAME to AttributeValue.S(name))
        }

        dynamoDbClient.use { ddb ->
            ddb.deleteItem(request)
        }
    }
}