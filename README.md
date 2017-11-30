# CassandraTransactionsPOC

#Scope

This just a proof of concept of a way how to simulate a transaction with isolation & atomicity in Apache Cassandra.
It is not by any means a transaction as we know it on the usual SQL databases.

The way it is implemented is by using a batch statement and conditional inserts into that batch. The insert statements
are "transactions" which are uniquely recognised by their transaction ids. The also have a modified date.

#Limitations
The batch statement with conditional inserts (aka using IF NOT EXISTS) is limited and can work only for a 
specific partition. It can not span on multiple tables and on multiple partitions. For that reason, the date
has been selected as the partition key.

#How to run
Just run a Cassandra instance and then run the unit test that is included on this project.

#How to run a Cassandra instance

A very fast way to do it is via Docker. By using the following commands:

```
docker run --name cassandra1 -p 7199:7199 -p 9042:9042 -d cassandra:3.11
```

This will fetch (if not already downloaded) a Cassandra image of version 3.11 and run it locally.