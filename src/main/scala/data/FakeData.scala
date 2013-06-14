package com.whereis.data

import akka.actor._
import akka.actor.ActorSystem
import akka.routing.RoundRobinRouter

sealed trait FakeDataMessage
case object WhereAreYou extends FakeDataMessage
case class Start(lat: Double, lon: Double, speed: Int, id: Int) extends FakeDataMessage
case class Located(id: Long, lat: Double, lon: Double, lag: Int)

// NE 40.79691751, -73.9323234   -- 86th ish
// NW 40.80991152, -73.9651107
// SW 40.74790721, -74.0064811     -- 23rd ish
// SE 40.73594165, -73.9759254

// lat 40.7610083, lon -73.99970499999999 me
// redis on port 6379

object Consts {
	val maxLat = 40.81
	val minLat = 40.73
	val maxLon = -73.93
	val minLon = -74.00
	val numberOfWorkers = 8
	val latUnit = 0.01
	val latRange = 8 
	val lonUnit = 0.01
	val lonRange = 7
}


class TransportActor extends Actor {

	var id = 0
	var lat = 0.0
	var lon = 0.0
	var speed = 0
	
	def receive = { 
		case WhereAreYou => {
			// move it a bit then return location
		}
	
		case Start(lt, ln, spd, i) => {
			lat = lt
			lon = ln
			speed = spd
			id = i
			sender ! "ok"
		}
	}
}

object Producer {
	import Consts._
	val rn = new scala.util.Random
	
	implicit val system = ActorSystem("WIFakeDataSystem")
	val transportRouter = system.actorOf(
			Props[TransportActor].withRouter(RoundRobinRouter(numberOfWorkers)), 
			name = "transportRouter")
			
	def startTransports = 
		for (i <- 0 until numberOfWorkers) 
			transportRouter ! Start(minLat + rn.nextInt(latRange) * latUnit, minLon + rn.nextInt(lonRange) * lonUnit, i %4, i)

}