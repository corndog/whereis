package com.whereis.data.imports

import com.whereis.data.imports.DataTypes._

//import scala.slick.driver.PostgresDriver
import scala.slick.driver.PostgresDriver.simple._ //{Session,Database}
import scala.slick.jdbc.StaticQuery.interpolation
import Database.threadLocalSession

object PostgresData {

	val password = "password"
	val user = "postgres"

// sql"select name from coffees where price < $limit".as[String].list
// INSERT INTO test (lat, lon, geom) values (40.761008,-73.999705, ST_SetSRID( ST_MakePoint(-73.999705, 40.761008), 4326) )

	def insertStop(s: Stop) = {
		Database.forURL("jdbc:postgresql:postgis20",
				driver = "org.postgresql.Driver",
				user = user,
				password = password) withSession {
			val stopId = s.stopId
			val agencyId = s.agencyId
			val code = s.code
			val name = s.name
			val direction = s.direction
			val locationType = s.locationType
			val lat = s.lat
			val lon = s.lon
			var rm = 0
			try {
				rm = sqlu"INSERT INTO stops (stop_id, agency_id, code, name, direction, location_type, lat, lon, geom ) VALUES ($stopId, $agencyId, $code, $name, $direction, $locationType, $lat, $lon, ST_SetSRID( ST_MakePoint($lon, $lat), 4326))".first		
			} catch {
				case e: java.lang.Throwable => println(e, "hopefully just a constraint violation")
				0
			}
			println("added " + rm)
			rm
		}
	}
	
	def test = {
		Database.forURL("jdbc:postgresql:postgis20",
				driver = "org.postgresql.Driver",
				user = user,
				password = password) withSession {
			val lat = 40.74
			val lon = -73.988
			val rm = sqlu"INSERT INTO TEST (lat, lon, geom) values ( $lat, $lon ,ST_SetSRID( ST_MakePoint($lon, $lat ), 4326))".first
			println("RM = " + rm)
			rm
		}
	}

	def insertRoute(r: Route) = {
		Database.forURL("jdbc:postgresql:postgis20",
				driver = "org.postgresql.Driver",
				user = user,
				password = password) withSession {
			val routeId = r.routeId
			val agencyId = r.agencyId
			val shortName = r.shortName
			val longName = r.longName
			val description = r.description
			val routeType = r.routeType
			val scheduleUrl = r.scheduleUrl
			val rm = sqlu"INSERT INTO routes VALUES ($routeId, $agencyId, $shortName, $longName, $description, $routeType, $scheduleUrl)".first		
			println("added " + rm)
			rm
		}
	}
	
	def insertRouteAtStop(rfs: RouteAtStop) = {
		Database.forURL("jdbc:postgresql:postgis20",
				driver = "org.postgresql.Driver",
				user = user,
				password = password) withSession {
			
			val agencyId = rfs.agencyId
			val stopId = rfs.stopId
			val routeId = rfs.routeId
			try {
				sqlu"INSERT INTO routes_at_stops VALUES ($agencyId, $stopId, $routeId)".first
			} catch {
				case e: java.lang.Throwable => println(e, "just a little exception, don't worry")
			}
		}
	}
	
	def getRoutesForAgency(agencyId: Long) = {
	
		Database.forURL("jdbc:postgresql:postgis20",
				driver = "org.postgresql.Driver",
				user = user,
				password = password) withSession {
			sql"SELECT route_id FROM routes where agency_id = $agencyId".as[String].list
		}
	}
}