name := "XLSpaceShipsPlay"
 
version := "1.0" 
      
lazy val `xlspaceshipsplay` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= List(
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.6.7",
  "com.softwaremill.sttp" %% "json4s" % "1.6.7",
  "org.json4s" %% "json4s-native" % "3.6.0",
  "org.json4s" %% "json4s-jackson" % "3.7.0-M2",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalamock" %% "scalamock" % "4.0.0" % "test",
  "org.mockito" % "mockito-core" % "2.18.3" % "test"
)

libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.0.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

      