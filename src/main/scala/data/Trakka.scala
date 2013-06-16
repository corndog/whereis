package com.whereis.data

import scala.xml._
import akka.actor._
import akka.actor.ActorSystem
import akka.routing.RoundRobinRouter

import com.whereis.data.redis.RedisData

sealed trait WIMessage
case class ItemsNear(lat:Double, lon:Double, lag: Int) extends WIMessage
case class Item(id: Long, lat: Double, lon: Double, lag:Int)
//case class Items(items:Seq[Item]) extends WIMessage

class ItemsWorker extends Actor {

	//val rn = new scala.util.Random

	val item1 = Item(1, 40.7610083, -73.99970499999999, 60)
	val item2 = Item(2, 40.7610090, -73.99970499999920, 90)
	val item3 = Item(3, 40.7610050, -73.99970499999820, 120)
	val items = Seq(item1, item2, item3)
	
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
	//val workerManager = system.actorOf(Props[ItemsWorker], name = "workerManager")
	val workerRouter = system.actorOf(
			Props[ItemsWorker].withRouter(RoundRobinRouter(numberOfWorkers)), 
			name = "workerRouter")

}

