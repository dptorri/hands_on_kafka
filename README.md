# hands_on_kafka

Apache Kafka features and examples

## Simple kafka setup in terminal
Install Kafka with brew
```
brew install kafka
```
Start Zookeeper
```
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
```
Start Kafka
```
kafka-server-start /usr/local/etc/kafka/server.properties

// In case of getting this error:
[2018-08-28 16:24:41,166] WARN [Controller id=0, targetBrokerId=0] 
Connection to node 0 could not be established. Broker may not be available. 
...
uncomment this line in /usr/local/etc/kafka/server.properties
listeners=PLAINTEXT://:9092
```
Create Kafka Topic called `order-topic`
```
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic order-topic
```
Initialize Producer console
```
kafka-console-producer --broker-list localhost:9092 --topic order-topic
>send first message
>send second message
```
Initialize Consumer console
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic order-topic --from-beginning
```
## Micronaut setup

1. create micronaut app  with the CLI
```
mn create-app --build=gradle --jdk=11 --lang=java --test=spock --features=kafka,netty-server com.example.order-svc-kafka
```

1.1 create a local configuration `resources/application-local.yml`
To make sure that this configuration is used when we launch the application, pass in the following
system property when launching the app: -Dmicronaut.environments=local.
```
micronaut:
  application:
    name: orderSvc
  server:
    port: 8080
``` 
1.2 Creating a Domain, Service, and Controller

1.2.1 create a class to represent the Order in a package called domain and add 4 properties: 
`id`, `customerId`, `totalCost` and `shipmentStatus` Add a constructor and getters/setters

1.2.2 Add a simple enum for the `ShipmentStatus`:
```
package com.example.domain;

public enum ShipmentStatus {
    PENDING, SHIPPED
}
```
1.2.3 create an OrderService to simulate a proper persistence tier.
Use a simple List to store orders in memory whilst the service is running.

1.2.4 The OrderController will have methods for listing orders, getting a single order, 
creating a new order, and updating an existing order. Nothing fancy, just a way to invoke 
our OrderService methods.

1. basic order microservice is now ready for action. We can start it up with:
```
./gradlew run -Dmicronaut.environments=local
```

### 2. Testing
2.1 First, let's add a few orders. Note that the ShipmentStatus will default to PENDING, 
so there is no need to pass that in when creating a new order.
2.2 We can now list all orders with a GET request to /order.
```
$ curl -s localhost:8080/order | jq
[
  {
    "id": 0,
    "customerId": 1,
    "totalCost": 19.17,
    "shipmentStatus": "PENDING"
  },
  {
    "id": 1,
    "customerId": 1,
    "totalCost": 9.44,
    "shipmentStatus": "PENDING"
  }
]
```
2.3 Or get a single order by passing in the order id.
```
$ curl -s localhost:8080/order/0 | jq
{
  "id": 0,
  "customerId": 1,
  "totalCost": 9.44,
  "shipmentStatus": "PENDING"
}
```
3. Publishing Orders
   We’ve set up Kafka and created a basic order service so far. In the next step,
   we’ll publish our orders to the order-topic that we set up earlier


