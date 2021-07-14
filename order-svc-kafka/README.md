# Order Service kafka
## Simple producer implementation

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
    port: 8081
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

### 2. Testing CRUD endpoint
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
### 3. Publishing Orders

3.1 We’ve set up Kafka and created a basic order service so far. In the next step,
we’ll publish our orders to the order-topic that we set up earlier
```
mn create-kafka-producer com.example.messaging.OrderProducer
| Rendered Kafka producer to src/main/java/com/example/messaging/OrderProducer.java
```
3.2 Which will create a basic producer thats empty
We just need to create a single message signature for sendMessage and annotate it with
@Topic to point it at our order-topic.
```
@KafkaClient
public interface OrderProducer {
    @Topic("order-topic")
    void sendMessage(@KafkaKey String key, String value);
}
```
3.3 Now we can inject our OrderProducer into our OrderService.
````
//OrderService.java
private final OrderProducer orderProducer;

public OrderService(OrderProducer orderProducer) {
    this.orderProducer = orderProducer;
}
````

3.4 And modify the newOrder method in OrderService to use the OrderProducer to publish the order
as a message to the topic. Note that the order will be serialized as JSON and the order JSON
will be the body of the message. We’re using a random UUID as the message key, but you can use
whatever unique identifier you’d like.
```
public Order newOrder(Order order) {
    order.setId((long) orders.size());
    this.orders.add(order);
    orderProducer.sendMessage(UUID.randomUUID().toString(), order);
    return order;
}
```
3.5 Before we can test this, make sure that you have the simple Kafka consumer still running in a
terminal window (or start a new consumer if you’ve already closed it from earlier) on the order-topic.
````
bin/kafka-console-consumer.sh --topic order-topic --bootstrap-server localhost:9092
````
3.6 Now add a new order to the system via cURL:
````
curl -s -H "Content-Type: application/json" -X POST -d '{"customerId": 1, "totalCost": 34.53}' localhost:8081/order | jq
{
"id": 2,
"customerId": 1,
"totalCost": 34.53,
"shipmentStatus": "PENDING"
}
````
3.7 And observe the consumer console which will receive the message for the new order!
```
kafka-console-consumer --topic order-topic --bootstrap-server localhost:9092

{"id":2,"customerId":1,"totalCost":34.53,"shipmentStatus":"PENDING"}
or
com.example.domain.Order@42a59089
```

