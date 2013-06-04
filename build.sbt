name := "whereis"

version := "1.0"

scalaVersion := "2.10.1"

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	"spray repo" at "http://repo.spray.io",
	"spray nightlies repo" at "http://nightlies.spray.io"
	)
 
libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.1.4",
	"io.spray" % "spray-can" % "1.1-20130516",
	"io.spray" % "spray-client" % "1.1-20130516",
	"io.spray" % "spray-routing" % "1.1-20130516"
  )