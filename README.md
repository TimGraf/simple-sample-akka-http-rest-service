## simple-sample-akka-http-rest-service (phone number validation service)

This service is pretty simple and does not have all of the components shown in the diagram below.  However the diagram shows the components that would be present in a more fully featured service.

![Architecture](docs/ArchitectureLayersDiagram.png)

### Framework

The service is built using [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala/http/index.html)

### Build

We use [sbt](http://www.scala-sbt.org) as our build tool

### Run

The following command will run the service

```
$ sbt run
```