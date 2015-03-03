package whereis.services

import LocationModels._
import whereis.db.LocationPersistence

trait LocationServices {

	object lp extends LocationPersistence
	import lp._

	def findStopsNear(lat: Double, lon: Double, radius: Double = 0.5): List[Point] = stopsNear(lat, lon, radius)
  def findRoutesForStop(stopCode: String): List[Point] = routesForStop(stopCode)
}