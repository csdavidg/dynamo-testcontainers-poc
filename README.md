# DynamoDB Testcontainers POC
This is a proof of concept project that demonstrates how to use Testcontainers to spin up a DynamoDB instance for testing.

## DynamoDBClient configuration using Prototype scope 
The AWS Kotlin SDK provides a DynamoDBClient which extends the Closable interface I decided to define it as a prototype bean, which means that a new instance of the bean is created every time it is requested from the container. Also with this proxy mode Spring will create a proxy object for the bean that will delegate method calls to the actual bean instance.

````
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
fun dynamoDBClient() = DynamoDbClient {
    region = dynamoDBConfig.region
    endpointUrl = Url.parse(dynamoDBConfig.endpoint)
}
````

## Prerequisites
Before running this project, make sure you have the following installed:

* Docker
* Java 11

## Running the Tests
To run the tests, use the following command:

```
./gradlew test
```

This will spin up a DynamoDB container using Testcontainers before running the tests, and then tear down the container after the tests have finished.

## Acknowledgments
This project was inspired by the [Localstack Testcontainers](https://www.testcontainers.org/modules/localstack/) and [AWS Kotlin SDK](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html) documentation.

## Problems faced

* #### Exception noSuchMethodError: 'okhttp3.Request$Builder okhttp3.Request$Builder.tag(kotlin.reflect.KClass, java.lang.Object)'

  More details https://github.com/awslabs/aws-sdk-kotlin/issues/765

  I resolved the problem adding the dependency 

    ````
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    ````

* #### Scoped bean injection problem

  The DynamoDBClient configuration shown above is necessary because of the way Spring initializes singleton beans. When a singleton bean is initialized, it requests an instance of the DynamoDBClient bean from the IoC container, and the container provides it with one. However, in subsequent uses, the same instance of the DynamoDBClient bean is used, and if it has already been used to interact with DynamoDB, it may not be safe to reuse. By configuring the DynamoDBClient bean with a prototype scope and a target class proxy mode, Spring ensures that a new instance of the DynamoDBClient bean is created every time it is requested, and that the instance is wrapped in a thread-safe proxy object to enable safe usage in a multi-threaded environment.

* #### BeforeAll Junit method

    By default, Junit 5 creates a new instance of the test class is created for each test method that is executed. 

    ```` 
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    ````
    The @TestInstance(TestInstance.Lifecycle.PER_CLASS) annotation is used to indicate that JUnit should create a single instance of MyTestClass and reuse it for both testAddItem() and testRemoveItem() methods. The @BeforeAll method is used to initialize the myList variable once, and then both test methods use it to perform their tests.