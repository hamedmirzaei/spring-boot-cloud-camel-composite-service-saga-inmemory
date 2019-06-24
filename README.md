# Spring Boot + Spring Data + Apache Camel (Service Composition Example)

This is a simple example to show you how to implement a `Saga EIP` over simple 
services using `Apache Camel` and `Spring Cloud`

# Libraries and Tools
* [Module] [`Spring Boot`](https://spring.io/projects/spring-boot)
* [Module] [`Spring Cloud`](https://spring.io/projects/spring-cloud)
* [Module] `Spring Data`
* [Library] [`Apache Camel`](https://camel.apache.org/)
* [ORM] `Hibernate` under abstraction of `Spring Data JPA`
* [Database] `MySQL on port 3306`
* [Tool] [`Locust`](https://locust.io/): Tool for load test
* [Library] [`DataFactory`](https://mvnrepository.com/artifact/org.fluttercode.datafactory/datafactory/0.8): Library for generating fake data


# How it works

Bellow is the relationships between different services provided in this demo.  

## Transaction and Account Demo

![Architecture](imgs/camel-cloud-saga-arch-transaction-account.png)


This is a common and simple banking business flow. There is a simple `Customer` and `Account` service. Each customer is 
connected to some accounts. Besides for each account, there is some number of transactions over it which is provided and
handled by `Transaction` service. 
There is also a `Camel` service which tries to implement `Saga EIP` over `Account` and `Transaction` services.
All these services are registered in `Eureka` as the service registry and discovery framework.

## Transfer from Bank A to Bank B Demo

![Architecture](imgs/camel-cloud-saga-arch-transfer.png)

This is another common and simple banking business flow. There is a simple `Bank-A` and `Bank-B` service. Each bank
has a repository of transactions. We are trying to exemplify a simple transaction between banks model using `Saga EIP`. 
There is another `Camel` service which tries to implement `Saga EIP` over `Bank-A` and `Bank-B` services.
All these services are registered in `Eureka` as the service registry and discovery framework.

# How to run
## Transaction and Account Demo
* Start `eureka-service` module. It can be verified using url [http://localhost:8761/](http://localhost:8761/).
* Start `account-service` module. It can be verified using url [http://localhost:8762/accounts](http://localhost:8762/accounts).
* Start `transaction-service` module. It can be verified using url [http://localhost:8763/transactions](http://localhost:8763/transactions).
* Start `camel-service` module. It can be verified using url [http://localhost:8764/health](http://localhost:8764/health).

## Transfer from Bank A to Bank B Demo
* Start `eureka-service` module. It can be verified using url [http://localhost:8761/](http://localhost:8761/).
* Start `bank-a-service` module. It can be verified using url [http://localhost:8765/transactions](http://localhost:8765/transactions).
* Start `bank-b-service` module. It can be verified using url [http://localhost:8766/transactions](http://localhost:8766/transactions).
* Start `camel-transfer-service` module. It can be verified using url [http://localhost:8767/health](http://localhost:8767/health).

# Load Test
You should have `Python` and `Locust` installed on your system to do the load test part. To do the load test
simply run the following through the terminal in project root path:

## Transaction and Account Demo
```
cd \path\to\project\spring-boot-cloud-camel-composite-service 
\path\to\locust\locust.exe -f load-test\locustfile.py
```

This starts the locust on  [http://localhost:8089](http://localhost:8089/)
You can set number of users and catch size and then start the test
It sends a lot of `Http.POST` requests to `http://localhost:8764/make-transactions`

## Transfer from Bank A to Bank B Demo
```
cd \path\to\project\spring-boot-cloud-camel-composite-service 
\path\to\locust\locust.exe -f load-test\locustfile-transfer.py
```

This starts the locust on  [http://localhost:8089](http://localhost:8089/)
You can set number of users and catch size and then start the test
It sends a lot of `Http.POST` requests to `http://localhost:8767/transfer`
