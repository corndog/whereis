package com.whereis

import scala.xml

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
import spray.routing.SimpleRoutingApp

import spray.httpx.encoding._
import spray.http.MediaTypes._
import spray.httpx.marshalling.Marshaller

import com.whereis.pages.Pages
import com.whereis.data._

object Main extends App with SimpleRoutingApp {
	implicit val system = ActorSystem("simple-routing-app")
	implicit val dispatcher = system.dispatcher // provide execution context for futures
	implicit val timeout = Timeout(5 seconds)
	
	val w = DataProducer.startTransports
	
	startServer(interface = "localhost", port = 5050) {
	
		path("home") { 
			getHtml( Pages.homePage )
		} ~
		path("spot") {
			get {
				parameters('lat.as[Double], 'lon.as[Double]) { (lat, lon) =>
					println("GET for location " + lat + " : " + lon)
					respondWithMediaType(`application/xml`) {
						complete { 
							(Trakka.workerRouter ? ItemsNear(lat, lon, 200)).mapTo[xml.Elem]
						}
					}
				}
			} ~
			post { 
				formFields('lat.as[Double], 'lon.as[Double]) { (lat, lon) =>
					println("POSTED Location??? " + lat + " : " + lon)
					respondWithMediaType(`application/xml`) {
						complete { 
							<xml><resp>ok</resp></xml>
							//(Trakka.workerRouter ? ItemsNear(lat, lon, 200)).mapTo[xml.Elem]
						}
					}
				}
			}
		} ~
		path("hello") {
			get { 
				respondWithMediaType(`text/html`) {
					complete { 
					<html>
						<body>
							<h1>HOLY CRAP</h1>
						</body>
					</html>
					}
				}
			}
		} ~
		path("things") {
			getHtml( 
				<html><body>
					<div>You GOT THINGS?</div>
					<form action="/things" method="POST">
						THING ? : <input type="text" name="thing"></input>
						<input type="submit" value="DO IT"></input>
					</form>
					</body></html>
			) ~
			post { 
				formFields('thing.as[String]) { thing =>
					println("NICE THING " + thing)
					redirect("/things")
				}
			}
		}
	}
	
	def getHtml(x: xml.Elem) = get { 
		respondWithMediaType(`text/html`) {
			complete { x }
		}
	}
	
}