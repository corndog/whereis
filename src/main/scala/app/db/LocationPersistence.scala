package whereis.db

import scalikejdbc._
import whereis.services.LocationModels._

trait LocationPersistence {

  Class.forName( "org.postgresql.Driver")
  ConnectionPool.singleton("jdbc:postgresql:postgis20", "postgres", "password")

  val toPoint: (WrappedResultSet => Point) = { rs => Point(rs.string("id"), rs.string("mref"), rs.string("typ"), rs.double("lat"), rs.double("lon"), rs.string("name")) }

	def stopsNear(lat: Double, lon: Double, radius: Double = 0.25): List[Point] = {
    val query = sql"SELECT stop_id as id, code as mref, 'stop' as typ, lat, lon, name FROM stops WHERE GeometryType(ST_Centroid(geom)) = 'POINT' AND ST_Distance_Sphere( ST_Point(ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom))), (ST_MakePoint($lon, $lat))) <= $radius * 1609.34"
    
    DB.readOnly { implicit session => query.map(toPoint).list.apply() }
    
  }

  // given a stop, find the other stops on the routes that
  // include that stop
  def routesForStop(code: String): List[Point] = {
    val query = sql"""
      select s.stop_id as id, s.code as mref, 'stop' as typ, s.lat, s.lon, s.name, ras.route_id 
      from stops s 
      left join routes_at_stops ras on s.stop_id = ras.stop_id
      where ras.route_id in (
        select r.route_id
          from stops st
          left join routes_at_stops rs on st.stop_id = rs.stop_id
          left join routes r on r.route_id = rs.route_id
          where st.code = $code
      )
    """

    DB.readOnly { implicit session => query.map(toPoint).list.apply() }
  }
}
object LocationPersitence extends LocationPersistence

// select s.code, r.route_id
// from stops s
// left join routes_at_stops ras on s.stop_id = ras.stop_id
// left join routes r on r.route_id = ras.route_id
// where s.code = '308209'
