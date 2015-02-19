package whereis.db

import scalikejdbc._
import whereis.services.LocationModels._

trait LocationPersistence {

  Class.forName( "org.postgresql.Driver")
  ConnectionPool.singleton("jdbc:postgresql:postgis20", "postgres", "0")

	def stopsNear(lat: Double, lon: Double, radius: Double = 0.5): List[Point] = {
    
    val query = sql"SELECT stop_id as id, code as mref, 'stop' as typ, lat, lon, name FROM stops WHERE GeometryType(ST_Centroid(geom)) = 'POINT' AND ST_Distance_Sphere( ST_Point(ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom))), (ST_MakePoint($lon, $lat))) <= $radius * 1609.34"
    
    DB.readOnly { implicit session =>
      query.map(rs => Point(rs.string("id"), rs.string("mref"), rs.string("typ"), rs.double("lat"), rs.double("lon"), rs.string("name"))).list.apply()
    }
    
  }
}
object LocationPersitence extends LocationPersistence