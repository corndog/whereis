package com.whereis

import akka.actor._

sealed trait WIMessage
case class ItemsNear(lat:Double, lon:Double, lag: Int) extends WIMessage
case class Item(id: Long, lat: Double, lon: Double, lag:Int)
//case class Items(items:Seq[Item]) extends WIMessage

// lat 40.7610083, lon -73.99970499999999

class ItemsWorker extends Actor {

	val rn = new scala.util.Random

	val item1 = Item(1, 40.7610083, -73.99970499999999, 60)
	val item2 = Item(2, 40.7610090, -73.99970499999920, 90)
	val item3 = Item(3, 40.7610050, -73.99970499999820, 120)
	val items = Seq(item1, item2, item3)
	
	// talk to some data store
	def findItems = items.take(3 - rn.nextInt(3))
	
	def receive = { 
		case ItemsNear(lat, lon, lag) =>
			sender ! { 
			val its = findItems
			// JSON was so 2013
			// <?xml version="1.0"?>  ummmm
			<xml><items>{ for (i <- its) yield <item><lat>{i.lat}</lat><lon>{i.lon}</lon></item> }</items></xml>
		}
	}
}

object Trakka {
	
	implicit val system = ActorSystem("WISystem")
	val workerManager = system.actorOf(Props[ItemsWorker], name = "workerManager")

}
