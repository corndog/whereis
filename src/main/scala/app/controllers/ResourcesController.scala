package whereis.controllers

import java.io.File
import org.parboiled.common.FileUtils
import scala.concurrent.duration._
import akka.actor._
import akka.pattern.ask
import spray.routing._
import spray.routing.directives.CachingDirectives
import spray.can.server.Stats
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import StatusCodes._
import Directives._
import MediaTypes._
import CachingDirectives._


// handle js, css, any other crap like that we have to deal with
trait ResourcesController extends AppController {
  // put stuff in src/main/resources/public
  // responds to /public/css/xyz, public/javascript/xyz

  val resourcesRoutes = {
    pathPrefix ("public") {
      pathPrefix("javascript") { 
        compressResponse() { getFromResourceDirectory("public/javascript") }
      } ~
      pathPrefix("css") {
        compressResponse() { getFromResourceDirectory("public/css") }
      }
    }
  }
}