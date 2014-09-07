package everplans.services

import spray.routing._
import spray.routing.authentication._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import redis.RedisClient
import java.util.UUID
import java.net.URI

import spray.http._

trait SessionServices {
  // put a sessionId in a cookie

  // case class HttpCookie(
  //   name: String,
  //   content: String,
  //   expires: Option[DateTime] = None,
  //   maxAge: Option[Long] = None,
  //   domain: Option[String] = None,
  //   path: Option[String] = None,
  //   secure: Boolean = false,
  //   httpOnly: Boolean = false,
  //   extension: Option[String] = None)


  implicit val actorSystem = whereis.controllers.Boot.system

  //val redisURI = new URI(sys.env("REDISTOGO_URL"))

  val redis = RedisClient()
    //if (redisURI.getHost.contains("localhost")) RedisClient() // localhost
    //else RedisClient(redisURI.getHost, redisURI.getPort, Some(redisURI.getUserInfo.split(":")(1)))

  val sessionCookieName = "wi-sessionId"
  val sixHours = 60 * 60 * 6 // expire things from redis

  val deleteSessionCookie = HttpCookie(
      sessionCookieName,
      "later",
      Some(DateTime(1970, 1, 1)), // spray has its own DateTime class
      Some(0),
      None,
      Some("/"),
      false,
      true,
      None
    )

  def createSessionCookie(userId: String) : HttpCookie = {
    val sessionId = UUID.randomUUID.toString

    // save userId under sessionId in redis
    redis.setex("wi_" + sessionId, sixHours, "figureoutdatalater")

    HttpCookie(
      sessionCookieName,
      sessionId,
      None, // => session cookie
      None,
      None,
      Some("/"),
      false,
      true,
      None
    )
  }

  
  object SessionCookieAuthenticator extends ContextAuthenticator[String] {
    val failure = Left(AuthenticationFailedRejection(AuthenticationFailedRejection.CredentialsRejected, List()))
    
    def apply(ctx: RequestContext): Future[Authentication[String]] = {
      ctx.request.cookies.find(_.name == sessionCookieName)
        .map(findUserDataFromCookie)
        .getOrElse(Future.successful(failure))
    }

    def findUserDataFromCookie(sessionCookie: HttpCookie): Future[Authentication[String]] = {
      val sessionId = sessionCookie.content
      redis.get[String]("wi_" + sessionId).map { _.map { Right(_) }.getOrElse(failure) }
    }
  }
}