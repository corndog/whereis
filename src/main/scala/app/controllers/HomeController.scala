package whereis.controllers

import akka.dispatch._
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import akka.actor.ActorSystem
import akka.routing.RoundRobinRouter
import akka.pattern.ask
import akka.util.Timeout
import reflect.ClassTag

import spray.httpx.encoding._
import spray.http.MediaTypes._
import spray.httpx.marshalling.Marshaller

import com.whereis.pages.Pages
//import com.whereis.data._

trait HomeController extends AppController {

	val homeRoutes = { 
		get {
			path("home") { 
				html { whereis.views.Home.homePage.mkString }
			} ~
			path("spot") {
				parameters('lat.as[Double], 'lon.as[Double], 'transtype.?) { (lat, lon, transtype) =>
					println("GET for location " + lat + " : " + lon + " transport type " + transtype)
					json {
						//(Trakka.workerRouter ? ItemsNear(lat, lon, 200)).mapTo[xml.Elem]
						s""" {data: []} """"
					}
				}
			}
		}
	}
}