# hands_on_kafka

Apache Kafka features and examples

## Project order-topic
- [Readme Order Service](https://github.com/dptorri/hands_on_kafka/blob/master/order-svc-kafka/README.md)

## Project microservices orders and trips
- [Readme Order Service](https://github.com/dptorri/hands_on_kafka/blob/master/order-svc-kafka/README.md)


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
kafka-topics --create --topic order-topic --bootstrap-server localhost:9092
```
Initialize Producer console
```
kafka-console-producer --topic order-topic --bootstrap-server localhost:9092
>send first message
>send second message
```
Initialize Consumer console
```
kafka-console-consumer --topic order-topic --from-beginning --bootstrap-server localhost:9092
```




