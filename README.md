# Project jms

## Description
This project was created aiming to learn how to make an integration between a consumer and a producer.
In this project, is possible to make a request to consumer and receive a message response.

## How to Use 
First, you'll need to start the server, for this, open the root directory in your
terminal and run the following instruction:

`mvn -pl jms-consumer-example clean compile exec:java`

When you have done it, then you can make the request.
In order to do this, you can just run the following instruction in a different terminal:

`mvn -pl jms-producer-example clean compile exec:java`
