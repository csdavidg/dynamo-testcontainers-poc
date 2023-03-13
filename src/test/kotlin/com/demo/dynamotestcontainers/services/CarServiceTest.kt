package com.demo.dynamotestcontainers.services

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableExists
import com.demo.dynamotestcontainers.config.DynamoDBBeansTestConfig
import com.demo.dynamotestcontainers.config.DynamoDBConfig
import com.demo.dynamotestcontainers.data.Car
import com.demo.dynamotestcontainers.exceptions.CarNotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

val LOCALSTACK_DOCKER_IMAGE: DockerImageName = DockerImageName.parse("localstack/localstack:latest")

@SpringBootTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = [DynamoDBBeansTestConfig::class])
class CarServiceTest {

    companion object {
        @Container
        val localStack: LocalStackContainer = LocalStackContainer(LOCALSTACK_DOCKER_IMAGE)
            .withServices(LocalStackContainer.Service.DYNAMODB)
            .waitingFor(Wait.forHealthcheck())
    }

    @Autowired
    lateinit var carService: ICarService

    @Autowired
    lateinit var dynamoDbClient: DynamoDbClient

    @Autowired
    lateinit var dynamoDBConfig: DynamoDBConfig

    @BeforeAll
    fun setUp() {
        runBlocking {
            createNewTable()
        }
    }

    @AfterAll
    fun cleanUp() {
        runBlocking {
            deleteDynamoDBTable(dynamoDBConfig.table)
        }
    }

    @Test
    fun createCarTest() {
        runBlocking {
            carService.createCar(Car(name = "Logan", model = 2012, brand = "Renault"))
            assertNotNull(carService.getCarByName("Logan"))
        }
    }

    @Test
    fun getCarTest() {
        runBlocking {
            carService.createCar(Car(name = "Logan", model = 2012, brand = "Renault"))
            assertNotNull(carService.getCarByName("Logan"))
        }

        assertThrows(CarNotFoundException::class.java) {
            runBlocking {
                carService.getCarByName("Logana")
            }

        }.apply {
            assertTrue(message.equals("The car was not found"))
        }
    }

    @Test
    fun deleteCarTest() {
        runBlocking {
            carService.createCar(Car(name = "Duster", model = 2012, brand = "Renault"))
            carService.deleteCar("Duster")
        }

        assertThrows(CarNotFoundException::class.java) {
            runBlocking {
                carService.getCarByName("Duster")
            }

        }.apply {
            assertTrue(message.equals("The car was not found"))
        }
    }

    suspend fun createNewTable(): String? {

        val attDef = AttributeDefinition {
            attributeName = "name"
            attributeType = ScalarAttributeType.S
        }

        val keySchemaVal = KeySchemaElement {
            attributeName = "name"
            keyType = KeyType.Hash
        }

        val provisionedVal = ProvisionedThroughput {
            readCapacityUnits = 10
            writeCapacityUnits = 10
        }

        val request = CreateTableRequest {
            attributeDefinitions = listOf(attDef)
            keySchema = listOf(keySchemaVal)
            provisionedThroughput = provisionedVal
            tableName = dynamoDBConfig.table
        }

        dynamoDbClient.use { ddb ->

            val response = ddb.createTable(request)
            ddb.waitUntilTableExists { // suspend call
                tableName = dynamoDBConfig.table
            }
            val tableArn = response.tableDescription!!.tableArn.toString()
            println("Table $tableArn is ready")
            return tableArn
        }
    }

    suspend fun deleteDynamoDBTable(tableNameVal: String) {

        val request = DeleteTableRequest {
            tableName = tableNameVal
        }

        dynamoDbClient.use { ddb ->
            ddb.deleteTable(request)
            println("$tableNameVal was deleted")
        }
    }
}