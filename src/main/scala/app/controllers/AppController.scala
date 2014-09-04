// App Controller
package whereis.controllers

import java.io.File
import org.parboiled.common.FileUtils
import scala.concurrent.duration._
import akka.actor._
import akka.pattern.ask
import spray.routing.{HttpService, RequestContext}
import spray.routing.directives.CachingDirectives
import spray.can.server.Stats
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import spray.routing._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.httpx.SprayJsonSupport._
import MediaTypes._
import CachingDirectives._

object ErrorModels {
  type Errors = Map[String,String]
  case class ErrorResponse(errors: Errors)
}

object ErrorsJsonProtocol extends DefaultJsonProtocol {
  import ErrorModels._
  implicit val ErrorFormat = jsonFormat1(ErrorResponse)
}

// this is just helpers and stuff.
trait AppController extends HttpService {
  import ErrorModels._
  import ErrorsJsonProtocol._
	
  implicit def executionContext = actorRefFactory.dispatcher

  val html = respondWithMediaType(`text/html`) & complete 
     
  // JSON NOTES: if you have a marshaller/unmarshaller for your scala object in scope you can
  // just complete with that object and spray will handle the serialization and mediatype stuff.
  // OTOH, if you want to construct your own json string you can finish the route with these two
  val json = respondWithMediaType(`application/json`) & complete

  val jsonOrNotFound = rejectEmptyResponse & json

  val completeOrNotFound = rejectEmptyResponse & complete
  
  val created = respondWithStatus(StatusCodes.Created) & complete

  def badRequestWithErrors(errs: Errors) = respondWithStatus(StatusCodes.BadRequest) & complete {  ErrorResponse(errs) }

  def createdIfValid[T : ToResponseMarshaller](f: => Either[Errors, T]) = f match {
    case Right(x: T) => created { x }
    case Left(errs: Errors) => badRequestWithErrors(errs)
  }

  def updatedIfValid[T : ToResponseMarshaller](f: => Either[Errors, Option[T]]) = f match {
    case Right(x: Option[T]) => completeOrNotFound(x)
    case Left(errs: Errors) => badRequestWithErrors(errs)
  }

  def layout(x: Seq[xml.Node], title:String): String =
    "<!DOCTYPE html>" ++ 
    (<html>
      <head>
        <title>{title}</title>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
      </head>
      <body>{x}</body>
    </html>).mkString
}