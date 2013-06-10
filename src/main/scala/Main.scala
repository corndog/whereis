package com.whereis

import scala.xml

import akka.dispatch._
//import scala.concurrent.ExecutionContext
//import scala.concurrent.Future
//import scala.concurrent.Await
import akka.util.Timeout
//import scala.concurrent.ExecutionContex
//import scala.concurrent.ExecutionContext
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import reflect.ClassTag
import spray.routing.SimpleRoutingApp

import spray.httpx.encoding._
import spray.http.MediaTypes._
import spray.httpx.marshalling.Marshaller

import com.whereis.pages.Pages

object Main extends App with SimpleRoutingApp {
	implicit val system = ActorSystem("simple-routing-app")
	implicit val dispatcher = system.dispatcher // provide execution context for futures
	implicit val timeout = Timeout(5 seconds)
	
	
	startServer(interface = "localhost", port = 5050) {
	
		path("home") { 
			getHtml( Pages.homePage )
		} ~
		path("spot") { 
			post { 
				formFields('spot.as[String]) { spot =>
					val lat :: lon :: Nil = spot.split("S").toList.map(_.toDouble)
					println("Location??? " + lat + " : " + lon)
					respondWithMediaType(`application/xml`) {
						complete { 
							(Trakka.workerManager ? ItemsNear(lat, lon, 200)).mapTo[xml.Elem]
							//ask(Trakka.workerManager, ItemsNear(lat, lon, 200)).mapTo[String]
							
							// fail, some marshaller problem..... 
							//its.map{ is =>
							//	(for (i <- is) yield <item><lat>{i.lat}</lat><lon>{i.lon}</lon></item>)
							//}
							//<items>{innerxml}</items>
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