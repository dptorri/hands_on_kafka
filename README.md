# hands_on_kafka

Apache Kafka features and examples

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
Create Kafka Topic called `chatroom`:
```
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic chatroom
```
Initialize Producer console
```
kafka-console-producer --broker-list localhost:9092 --topic chatroom
>send first message
>send second message
```
Initialize Consumer console
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic chatroom --from-beginning
```
