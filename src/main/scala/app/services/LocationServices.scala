package whereis.services

import whereis.db.LocationPersistence

trait LocationServices {

	object lp extends LocationPersistence
	import lp._

	def findStopsNear(lat: Long, lon: Long, radius: Int = 0.5): List[(String, Long, Long)] = stopsNear(lat, lon, radius)
}