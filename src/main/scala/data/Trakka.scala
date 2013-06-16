package com.whereis.data

import scala.xml._
import akka.actor._
import akka.actor.ActorSystem
import akka.routing.RoundRobinRouter

import com.whereis.data.redis.RedisData

sealed trait WIMessage
case class ItemsNear(lat:Double, lon:Double, lag: Int) extends WIMessage
case class Item(id: Long, lat: Double, lon: Double, lag:Int)

class ItemsWorker extends Actor {
	
	// talk to some data store
	// redis because its easy to set up
	// one day actually use lat, lon....
	def findItemsNear(lat: Double, lon:Double, lag:Int) =
		RedisData.redisClt.lrange(RedisData.listName, 0, 7).map(XML.loadString(_))
	
	def receive = { 
		case ItemsNear(lat, lon, lag) =>
			sender ! <xml><items>{ findItemsNear(lat,lon,lag) }</items></xml>
	}
}

object Trakka {
	val numberOfWorkers = 8
	implicit val system = ActorSystem("WISystem")
	val workerRouter = system.actorOf(
			Props[ItemsWorker].withRouter(RoundRobinRouter(numberOfWorkers)), 
			name = "workerRouter")

}

