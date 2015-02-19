import sbt._
import Keys._

object WIBuild extends Build {
  /* Dependencies */
  //val localMavenRepo = "Local Maven Repository" at ""+ Path.userHome + "/.m2/repository"
 // val fwbrasilRepo = "fwbrasil.net" at "http://fwbrasil.net/maven/"
	val rediscalaRepo = "rediscala" at "http://dl.bintray.com/etaty/maven"
	
	val typesafeRepo = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
	val sonatypeRepo = "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"

  // val activateVersion = "1.6.2"
  // val activateCore = "net.fwbrasil" %% "activate-core" % activateVersion
  // val activateJdbc = "net.fwbrasil" %% "activate-jdbc" % activateVersion

  val postgresql = "org.postgresql" % "postgresql" % "9.3-1100-jdbc41"

  val akkaV = "2.3.0"
  val sprayV = "1.3.1"

  val sprayCaching = "io.spray" % "spray-caching" % sprayV
  val sprayJson = "io.spray" %%  "spray-json" % "1.2.6"
  val sprayCan = "io.spray" % "spray-can" % sprayV
  val sprayRouting = "io.spray" % "spray-routing" % sprayV
  val sprayClient = "io.spray" % "spray-client" % sprayV
  val sprayTest = "io.spray"            %   "spray-testkit" % sprayV  % "test"
  val akkaActor = "com.typesafe.akka"   %%  "akka-actor"    % akkaV
  val akkaTest = "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test"
	
	//val slick = "com.typesafe.slick" %% "slick" % "2.1.0"
	val slf4j = "org.slf4j" % "slf4j-nop" % "1.6.4"
	
  val jbcrypt = "org.mindrot" % "jbcrypt" % "0.3m"

  val jodaTime = "joda-time" % "joda-time" % "2.3"
  
  val rediscala = "com.etaty.rediscala" %% "rediscala" % "1.3.1"

  //val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
  val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % "2.2.3"
  
  val logback = "ch.qos.logback"  %  "logback-classic"   % "1.1.2"

  lazy val whereis = Project(
    id = "whereis",
    base = file("."),
    settings = Defaults.defaultSettings ++ Seq(
        libraryDependencies ++= Seq(scalikejdbc, logback, slf4j, postgresql, sprayCaching, sprayJson, sprayCan, sprayRouting, sprayClient, sprayTest, akkaActor, akkaTest, jbcrypt, rediscala /*, scalaCheck */),
        organization := "com.whereis",
        scalaVersion := "2.10.3",
        version := "1.0",
        resolvers ++= Seq(/*localMavenRepo,*/ typesafeRepo, sonatypeRepo, rediscalaRepo),
        scalacOptions ++= Seq("-deprecation", "-feature")
      )
	)
}