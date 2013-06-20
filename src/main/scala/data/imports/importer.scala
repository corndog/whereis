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

import scala.xml._

// request and respone in 
// https://github.com/spray/spray/blob/release/1.1/spray-http/src/main/scala/spray/http/HttpMessage.scala

object Importer {

	implicit val system = ActorSystem()
	import system.dispatcher
	
	val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
	
	def getNYCRoutes = {
		val url = "http://bustime.mta.info/api/where/routes-for-agency/MTA%20NYCT.xml?key=e1b2037e-dc89-4e24-9266-2489d78cacdb"
		val response: Future[HttpResponse] = pipeline(Get(url))
		
		response onComplete {
			case Success(resp) => {
				val xml:Elem = XML.loadString(resp.entity.asString)
				val respcode:String = ((xml \ "code") text) // lack of parens confused compiler!
				println("RESPONSE CODE " + respcode.toString)
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

