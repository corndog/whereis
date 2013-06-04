package com.whereis

import scala.xml

import scala.concurrent.duration._
import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import spray.http.MediaTypes._

import com.whereis.pages.Pages

object Main extends App with SimpleRoutingApp {
	implicit val system = ActorSystem("simple-routing-app")

	startServer(interface = "localhost", port = 5050) {
	
		path("home") { 
			getHtml( Pages.homePage )
		} ~
		path("spot") { 
			post { 
				formFields('spot.as[String]) { spot =>
					val lat :: lon :: Nil = spot.split("S").toList
					println("Location??? " + lat + " : " + lon)
					respondWithMediaType(`text/html`) { 
						complete { "OK" }
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