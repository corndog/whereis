package whereis.controllers

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import util.Properties

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("whereis-actors")

  // create and start our service actor
  val service = system.actorOf(Props[ControllerActor], "whereis")

  // start a new HTTP server on port 5000 with our service actor as the handler
  IO(Http) ! Http.Bind(service, "localhost", port = 5150)

}