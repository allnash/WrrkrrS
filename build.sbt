import NativePackagerHelper._

name := """wrrkrr-server"""

version := "0.1"

maintainer := "Nash Gadre <gadre@omegatrace.com>"

packageSummary := "OmegaTrace WrrKrr Engage Platform "

packageDescription := """Owned by OmegaTrace Inc. Copyright 2018."""

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  evolutions,
  javaJdbc,
  javaWs,
  guice,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.46",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.typesafe.play" %% "play-json" % "2.6.0",
  "org.lightcouch" % "lightcouch" % "0.1.8",
  "io.netty" % "netty-all" % "4.0.30.Final",
  "com.ocpsoft" % "ocpsoft-pretty-time" % "1.0.7",
  "org.eclipse.jetty.npn" % "npn-api" % "8.1.2.v20120308",
  "org.eclipse.jetty.alpn" % "alpn-api" % "1.0.0",
  "org.apache.commons" % "commons-email" % "1.3.1",
  "commons-validator" % "commons-validator" % "1.6",
  "com.mashape.unirest" % "unirest-java" % "1.4.9",
  "net.gpedro.integrations.slack" % "slack-webhook" % "1.3.0",
  "commons-codec" % "commons-codec" % "1.10",
  "com.google.zxing" % "javase" % "3.2.1",
  "javax.mail" % "mail" % "1.4.7",
  "com.github.triologygmbh" % "reCAPTCHA-V2-java" % "1.0.1",
  "org.ektorp" % "org.ektorp" % "1.4.4",
  "com.mashape.unirest" % "unirest-java" % "1.4.9",
  "com.lemmingapex.trilateration" % "trilateration" % "1.0.2",
  "io.ebean" % "ebean" % "11.15.3",
  "com.github.javafaker" % "javafaker" % "0.15",
  "com.neovisionaries" % "nv-oui" % "1.1",
  "org.msgpack" % "msgpack-core" % "0.8.16"
)

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.10.11"
libraryDependencies += "org.webjars" % "swagger-ui" % "2.2.0"
libraryDependencies += "com.github.0xbaadf00d" % "ebean-encryption" % "release~17.01"

swaggerDomainNameSpaces := Seq("models")

lazy val server = (project in file(".")).enablePlugins(PlayJava, PlayEbean, SwaggerPlugin)

// Temporary fix fro warnings
// TODO: This is needed to make Play 2.6.12 work. This may be removed if warnings are resolved in future.
// https://github.com/playframework/playframework/issues/7832#issuecomment-336014319
// https://github.com/playframework/playframework/blob/2.6.x/framework/project/Dependencies.scala#L11
val akkaVersion = "2.5.11"

dependencyOverrides ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion,
  "com.typesafe.akka" % "akka-stream_2.12" % akkaVersion,
  "com.google.guava" % "guava" % "23.0",
  "org.webjars" % "webjars-locator-core" % "0.33",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "io.ebean" % "ebean" % "11.15.3",
  "org.avaje" % "avaje-classpath-scanner" % "2.2.4",
  "org.codehaus.plexus" % "plexus-utils" % "3.0.17"
)

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.194"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Add scaner directory to the Node scanner package.
mappings in Universal ++= directory("data")

publishArtifact in (Compile, packageDoc) := false

enablePlugins(JavaServerAppPackaging)