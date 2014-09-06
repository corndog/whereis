package whereis.db

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import scala.slick.jdbc.StaticQuery.interpolation

import whereis.services.LocationModels._

trait LocationPersistence {

	val password = "password"
	val user = "postgres"
	val dbUrl = "jdbc:postgresql:postgis20"
	val db = Database.forURL(dbUrl, "org.postgresql.Driver")

	def stopsNear(lat: Double, lon: Double, radius: Double = 0.5): List[Point] = {
		val query = sql"SELECT stop_id, 'stop', lat, lon, name FROM stops WHERE GeometryType(ST_Centroid(geom)) = 'POINT' AND ST_Distance_Sphere( ST_Point(ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom))), (ST_MakePoint($lon, $lat))) <= $radius * 1609.34".as[Point]
		db.withDynSession { implicit session =>
			query.list
		}
	}

}