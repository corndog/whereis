package whereis.controllers

import akka.dispatch._
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import reflect.ClassTag

import spray.httpx.encoding._
import spray.http.MediaTypes._
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.httpx.SprayJsonSupport._

import spray.json._
import DefaultJsonProtocol._

import whereis.services.LocationServices
import whereis.services.LocationModels._
import whereis.services.LocationModelsJsonProtocol._

trait HomeController extends AppController {

  object ls extends LocationServices

	val homeRoutes = { 
		get {
			path("home") { 
				html { whereis.views.Home.homePage.mkString }
			} ~
			path("stops") {
				parameters('lat.as[Double], 'lon.as[Double], 'radius.?.as[Option[Double]], 'transtype.? ) { (lat, lon, radius, transtype) =>
					complete { Points(ls.findStopsNear(lat, lon, radius.getOrElse(0.5))) }
				}
			}
		}
	}
}