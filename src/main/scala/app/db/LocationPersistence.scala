package whereis.db

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.GetResult

trait LocationPersistence {

	val password = "password"
	val user = "postgres"
	val dbUrl = "jdbc:postgresql:postgis20"
	val db = Database.forURL(dbUrl, driver=jdbcDriver, user = user, password = password)

	def findStopsNear(lat: Long, lon: Long, radius: Int = 0.5): List[(String, Long, Long)] = {
		val query = sql"SELECT stop_id, lat, lon FROM stops WHERE GeometryType(ST_Centroid(geom)) = 'POINT' AND ST_Distance_Sphere( ST_Point(ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom))), (ST_MakePoint($lon, $lat))) <= $radius * 1609.34".as[(String, Double, Double)]
		db.withSession { implicit session =>
			query.list
		}
	}

}