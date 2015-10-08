import com.atlassian.labs.gitstamp.GitStampPlugin._
import com.typesafe.sbt.packager.SettingsHelper._
import spray.revolver.RevolverPlugin.Revolver

name := """phone-number-validation"""

organization := "org.grafx"

lazy val root = (project in file(".")).enablePlugins(SbtNativePackager, JavaAppPackaging, UniversalDeployPlugin)

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Maven Releases" at "http://repo.typesafe.com/typesafe/maven-releases",
  "Teachscape" at "http://teachscape.artifactoryonline.com/teachscape/repo"
)

libraryDependencies ++= {
  val akkaVersion       = "2.3.12"
  val akkaStreamVersion = "1.0"
  val scalaTestVersion  = "2.2.4"

  Seq(
    "ch.qos.logback"               % "logback-classic"                   % "1.1.3",
    "com.typesafe.scala-logging"  %% "scala-logging"                     % "3.1.0",
    "com.typesafe.akka"           %% "akka-actor"                        % akkaVersion,
    "com.typesafe.akka"           %% "akka-slf4j"                        % akkaVersion,
    "com.typesafe.akka"           %% "akka-testkit"                      % akkaVersion % "test",
    "com.typesafe.akka"           %% "akka-stream-experimental"          % akkaStreamVersion,
    "com.typesafe.akka"           %% "akka-http-core-experimental"       % akkaStreamVersion,
    "com.typesafe.akka"           %% "akka-http-experimental"            % akkaStreamVersion,
    "com.typesafe.akka"           %% "akka-http-spray-json-experimental" % akkaStreamVersion,
    "com.typesafe.akka"           %% "akka-http-testkit-experimental"    % akkaStreamVersion,
    "com.teachscape"              %% "mothership"                        % "1.1.3",
    "org.scalatest"               %% "scalatest"                         % scalaTestVersion  % "test",
    "io.gatling.highcharts"        % "gatling-charts-highcharts"         % "2.1.7"
  )
}

// An SBT plugin that add some basic git data to the artefact's MANIFEST.MF file.
// https://bitbucket.org/pkaeding/sbt-git-stamp
Seq( gitStampSettings: _* )

// continuous build
Revolver.settings: Seq[sbt.Def.Setting[_]]

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

// deploy
deploymentSettings

publishMavenStyle := true

// sbt-native-packager - universal:publish
makeDeploymentSettings(Universal, packageZipTarball in Universal, "tgz")

// sbt-release - publish
val packageTgz = taskKey[File]("package-zip-tarball")

packageTgz := (baseDirectory in Compile).value / "target" / "universal" / (name.value + "-" + version.value + ".tgz")

artifact in (Universal, packageTgz) ~= { (art:Artifact) => art.copy(`type` = "tgz", extension = "tgz") }

addArtifact(artifact in (Universal, packageTgz), packageTgz in Universal): Seq[sbt.Def.Setting[_]]

publish <<= publish dependsOn (packageZipTarball in Universal)

publishTo := {
  val artifactory = "https://teachscape.artifactoryonline.com/teachscape/"

  if (version.value.endsWith("SNAPSHOT"))
    Some("Teachscape Artifactory Snapshots Local" at artifactory + "libs-snapshots-local")
  else
    Some("Teachscape Artifactory Releases Local" at artifactory + "libs-releases-local")
}

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")