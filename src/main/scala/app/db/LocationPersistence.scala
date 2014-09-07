package whereis.db

import java.sql._
import whereis.services.LocationModels._
import DBContext._

trait LocationPersistence {

	def stopsNear(lat: Double, lon: Double, radius: Double = 0.5): List[Point] = {
    
    val query = s"SELECT stop_id, 'stop', lat, lon, name FROM stops WHERE GeometryType(ST_Centroid(geom)) = 'POINT' AND ST_Distance_Sphere( ST_Point(ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom))), (ST_MakePoint($lon, $lat))) <= $radius * 1609.34"
    
    var conn: Connection = null
    var stmt: Statement = null
    var rs: ResultSet = null
    val result = scala.collection.mutable.ArrayBuffer.empty[Point]
    try {
      conn = storage.directAccess //java.sql.Connection
      stmt = conn.createStatement()
      rs = stmt.executeQuery(query)
      while (rs.next()) {
        result += Point( rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4), rs.getString(5))
      }
      result.toList
    } finally {
      if (rs != null) rs.close
      if (stmt != null) stmt.close
      if (conn != null) conn.close
    }
  }
}