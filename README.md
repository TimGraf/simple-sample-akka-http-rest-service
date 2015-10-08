## Shape-Shifter

MASTER: [![Build Status](http://jenkins.teachscape.com:8080/buildStatus/icon?job=shape-shifter)](http://jenkins.teachscape.com:8080/buildStatus/icon?job=shape-shifter)

This service is responsible for 

### Details


Wishes


### Stores



### Framework

The service is built using [Akka Streams](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala/stream-index.html) and [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala/http/index.html)

### Build

We use [sbt](http://www.scala-sbt.org) as our build tool

### Run

The following command will run the service

```
$ sbt run
```

### Snapshot

We use [sbt-native-packager](http://www.scala-sbt.org/sbt-native-packager) as the base of our snapshot process

```
$ sbt universal:packageZipTarball universal:publish
```

### Release

We use [sbt-native-packager](http://www.scala-sbt.org/sbt-native-packager) and [sbt-release](https://github.com/sbt/sbt-release) as the base of our release process

### Endpoint Documentation

Click [here](http://localhost:8080/) API documentation

### Load Testing

We use [Gatling](http://gatling.io) as our load testing tool

[Here](src/test/scala/com/teachscape/simulations) are the simulations and [here]() are the results

### Setup

Follow [these](docs/SETUP.md) directions
=======