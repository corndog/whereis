package com.whereis.data.imports

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import akka.actor._
import akka.pattern.ask
import akka.event.Logging
import akka.io.IO
import spray.can.Http
import spray.http._
import spray.client.pipelining._
import spray.util._
import scala.collection.mutable.{Set => MSet}
import java.net.URLEncoder

import scala.xml._

// request and response in 
// https://github.com/spray/spray/blob/release/1.1/spray-http/src/main/scala/spray/http/HttpMessage.scala

object DataTypes {

	case class RouteAtStop(agencyId: Long, stopId:String, routeId: String)

	case class Stop(
		stopId: String,
		agencyId: Long,
		code: String,
		name: String,
		direction: String,
		locationType: Int,
		lat: Double,
		lon: Double
	)

	case class Route(
		routeId: String,
		agencyId: Long,
		shortName: String,
		longName: String,
		description: String,
		routeType: Int,
		scheduleUrl: String
	)
}

object Importer {
	import DataTypes._

	implicit val system = ActorSystem()
	import system.dispatcher
	
	val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
	
	def getNYCStops = {
		val routes = PostgresData.getRoutesForAgency(1)
		routes.reverse.dropWhile(_ != "MTA NYCT_BX36").foreach(r => { loadStopsForRoute(r); println("LOADED " + r); Thread.sleep(20000) } )
	}
	
	def loadStopsForRoute(routeId:String) = {
		println("FOR ROUTE " + routeId)
		val url = "http://bustime.mta.info/api/where/stops-for-route/" + routeId.replace(" ", "%20") + ".xml?key=e1b2037e-dc89-4e24-9266-2489d78cacdb"
		println(url)
		val response: Future[HttpResponse] = pipeline(Get(url))

		response onComplete {
			case Success(resp) => {
				val xml:Elem = XML.loadString(resp.entity.asString)
				val respcode:String = ((xml \ "code") text) // lack of parens confused compiler!
				println("RESPONSE CODE " + respcode.toString)
				val stops = xml \\ "stop"
				stops.foreach{stop =>
					val routes = (stop \\ "route")
					val routeIds = routes.map(r => (r \ "id").text).toArray
					val stopId = (stop \ "id").text
					val s = Stop(
						stopId,
						1,
						(stop \ "code").text,
						(stop \ "name").text,
						(stop \ "direction").text,
						(stop \ "locationType").text.toInt,
						(stop \ "lat").text.toDouble,
						(stop \ "lon").text.toDouble
					)
					// both of these will suffer duplicates, need to handle that insert
					PostgresData.insertStop(s)
					routeIds.foreach(rid => PostgresData.insertRouteAtStop(RouteAtStop(1, stopId, rid)))
				}
				
				
			}
			case Failure(error) => { 
				println("ERRORR " + error)
				shutdown()
			}
		}
	}
	
	def loadNYCRoutes = {
		val url = "http://bustime.mta.info/api/where/routes-for-agency/MTA%20NYCT.xml?key=e1b2037e-dc89-4e24-9266-2489d78cacdb"
		val response: Future[HttpResponse] = pipeline(Get(url))
		
		response onComplete {
			case Success(resp) => {
				val xml:Elem = XML.loadString(resp.entity.asString)
				val respcode:String = ((xml \ "code") text) // lack of parens confused compiler!
				println("RESPONSE CODE " + respcode.toString)
				val routes = xml \\ "route"
				println("Routes : " + routes.size)
				routes.foreach{ route =>
					val rt = Route(
						(route \ "id").text,
						1,
						(route \ "shortName").text,
						(route \ "longName").text,
						(route \ "description").text,
						(route \ "type").text.toInt,
						(route \ "url").text
					)
					PostgresData.insertRoute(rt)
				}
				shutdown()
			}
			case Failure(error) => { 
				println("ERRORR " + error)
				shutdown()
			}
		}
	}

	def shutdown(): Unit = {
		IO(Http).ask(Http.CloseAll)(1.second).await
		system.shutdown()
	}
}

