## simple-sample-akka-http-rest-service (phone number validation service)

This service is pretty simple and does not have all of the components shown in the diagram below.  However the diagram shows the components that would be present in a more fully featured service.

![Architecture](docs/ArchitectureLayersDiagram.png)

### Framework

The service is built using [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala/http/index.html)

### Build

We use [sbt](http://www.scala-sbt.org) as our build tool

```
sbt clean compile test
```

### Run

The following command will run the service

```
$ sbt run
```

or with Docker

```
sbt docker:publishLocal
eval "$(docker-machine env default)"
docker run -it phone-number-validation:0.1-SNAPSHOT
```

When running locally the Swagger API documentation is available at the following URL

http://localhost:9000/swagger/index.html