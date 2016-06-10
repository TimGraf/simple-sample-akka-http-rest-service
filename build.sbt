import com.atlassian.labs.gitstamp.GitStampPlugin._

name := """phone-number-validation"""

organization := "org.grafx"

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging, DockerPlugin)

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Maven Releases" at "http://repo.typesafe.com/typesafe/maven-releases"
)

libraryDependencies ++= {
  val akkaVersion       = "2.4.7"
  val scalaTestVersion  = "2.2.4"

  Seq(
    "ch.qos.logback"                % "logback-classic"                   % "1.1.3",
    "com.typesafe.scala-logging"   %% "scala-logging"                     % "3.1.0",
    "com.typesafe.akka"            %% "akka-actor"                        % akkaVersion,
    "com.typesafe.akka"            %% "akka-slf4j"                        % akkaVersion,
    "com.typesafe.akka"            %% "akka-testkit"                      % akkaVersion % "test",
    "com.typesafe.akka"            %% "akka-http-experimental"            % akkaVersion,
    "com.typesafe.akka"            %% "akka-http-spray-json-experimental" % akkaVersion,
    "org.typelevel"                %% "cats"                              % "0.5.0",
    "com.github.swagger-akka-http" %% "swagger-akka-http"                 % "0.7.0",
    "org.scalatest"                %% "scalatest"                         % scalaTestVersion  % "test",
    "io.gatling.highcharts"         % "gatling-charts-highcharts"         % "2.1.7"           % "test",
    "io.gatling"                    % "gatling-test-framework"            % "2.1.7"           % "test"
  )
}

// An SBT plugin that add some basic git data to the artefact's MANIFEST.MF file.
// https://bitbucket.org/pkaeding/sbt-git-stamp
Seq( gitStampSettings: _* )

// run options
javaOptions in run ++= Seq(
  "-Dconfig.file=src/main/resources/application.conf",
  "-Dlogback.configurationFile=src/main/resources/logback.xml"
)

scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

// test options
javaOptions in Test += "-Dconfig.file=src/test/resources/application.conf"

scalacOptions in Test ++= Seq("-Yrangepos")

fork := true

Revolver.settings

mappings in Docker += {
  (resourceDirectory in Compile).value / "application.conf" -> "/opt/docker/conf/application.conf"
  (resourceDirectory in Compile).value / "global.conf" -> "/opt/docker/conf/global.conf"
  (resourceDirectory in Compile).value / "logback.xml" -> "/opt/docker/conf/logback.xml"
}

dockerExposedPorts := Seq(9000)
dockerEntrypoint := Seq("bin/%s" format executableScriptName.value, "-Dconfig.file=/opt/docker/conf/application.conf")