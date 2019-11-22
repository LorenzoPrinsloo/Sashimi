lazy val akkaHttpVersion = "10.1.9"
lazy val akkaVersion     = "2.6.0-M5"
lazy val circeVersion    = "0.11.1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "roflsoft",
      scalaVersion    := "2.12.8"
    )),
    envVars := Map(
      "WEB_INTERFACE" -> "localhost",
      "WEB_PORT" -> "9000"
    ),
    name := "sashimi",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.27.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic"  % circeVersion,
      "io.circe" %% "circe-parser"  % circeVersion,
      "io.monix" %% "monix" % "3.0.0-RC4",
      "org.tpolecat" %% "doobie-core"      % "0.7.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.postgresql" % "postgresql" % "42.2.8",
      "io.roflsoft" %% "sushi" % "0.0.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "net.debasishg" %% "redisclient" % "3.10",
      "com.beachape" % "enumeratum_2.12" % "1.5.13",
      "com.beachape" % "enumeratum-doobie_2.12" % "1.5.15",
      "com.beachape" % "enumeratum-circe_2.12" % "1.5.15",
      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test
    )
  )
