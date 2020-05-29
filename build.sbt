lazy val akkaHttpVersion = "10.1.11"
lazy val akkaVersion     = "2.5.26"
lazy val circeVersion    = "0.12.0"

mainClass in assembly := Some("roflsoft.Server")

lazy val root = (project in file(".")).
  settings(
    commands ++= Seq(CodeGen.crud),
    inThisBuild(List(
      organization    := "roflsoft",
      scalaVersion    := "2.13.0"
    )),
    envVars := Map(
      "WEB_INTERFACE" -> "localhost",
      "WEB_PORT" -> "9000"
    ),
    scalacOptions ++= Seq(
      "-language:postfixOps"
    ),
    name := "sashimi",
    resolvers += "jitpack".at("https://jitpack.io"),
    libraryDependencies ++= Seq(
      // Validation
      "com.github.krzemin" %% "octopus" % "0.4.1",
      "com.github.t3hnar" %% "scala-bcrypt" % "4.1",
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.30.0",
      "net.codingwell" %% "scala-guice" % "4.2.6",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic"  % circeVersion,
      "io.circe" %% "circe-parser"  % circeVersion,
      "io.circe" %% "circe-literal"  % circeVersion,
      "io.circe" %% "circe-optics"  % circeVersion,
      "io.monix" %% "monix" % "3.1.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.postgresql" % "postgresql" % "42.2.8",
      "io.getquill" %% "quill-jdbc-monix" % "3.5.1",
      "io.roflsoft" %% "sushi" % "0.0.5",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "net.debasishg" %% "redisclient" % "3.10",
      "com.beachape" % "enumeratum-circe_2.13" % "1.5.21",
      "org.scalatest" %% "scalatest" % "3.1.0" % Test,
      "io.circe" %% "circe-json-schema" % "0.1.0",
      "com.typesafe" % "config" % "1.4.0",
      "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % "1.1.2"
    )
  )


