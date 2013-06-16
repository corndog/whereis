package com.whereis.data

import scala.xml._
import scala.concurrent.duration._
import akka.actor._
import akka.routing.RoundRobinRouter

import com.top10.redis._

import com.whereis.data.redis.RedisData

sealed trait FakeDataMessage
case object StartAll extends FakeDataMessage
case object Move extends FakeDataMessage
case object MoveAll extends FakeDataMessage
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
	import Consts._

	var id = 0
	var lat = 0.0
	var lon = 0.0
	var speed = 0
	def latStep = speed * latUnit / 100
	def lonStep = speed * lonUnit / 100
	
	def move = {
		lat += latStep
		lon += lonStep
	}
	
	def strLoc = <item id={id.toString}><lat>{lat}</lat><lon>{lon}</lon></item>
	
	def receive = { 
		case Move => {
			// move it a bit, save to redis
			move
			RedisData.redisClt.lpush(RedisData.listName, strLoc.toString)
		}
	
		case Start(lt, ln, spd, i) => {
			lat = lt
			lon = ln
			speed = spd
			id = i
		}
	}
}

class DataController extends Actor {
	import Consts._
	val rn = new scala.util.Random

	val transportRouter = context.actorOf(
			Props[TransportActor].withRouter(RoundRobinRouter(numberOfWorkers)), 
			name = "transportRouter")
			
	def receive =  {
		case StartAll => {
		println("START FAKE DATA")
		for (i <- 0 until numberOfWorkers) 
			transportRouter ! Start(minLat + rn.nextInt(latRange) * latUnit, minLon + rn.nextInt(lonRange) * lonUnit, i %4, i)
		}
		
		case MoveAll => {
			println("MOVE ALL")
			for (i <- 0 until numberOfWorkers)
				transportRouter ! Move
		}
	}

}

object DataProducer {
	import Consts._
	
	implicit val system = ActorSystem("WIFakeDataSystem")
	import system.dispatcher 
	
	val transportController = system.actorOf(
			Props[DataController],
			name = "transportController")
			
	def startTransports =  
			transportController ! StartAll
	
	val cancellable = system.scheduler.schedule(1000 milliseconds, 5000 milliseconds, transportController, MoveAll)
}