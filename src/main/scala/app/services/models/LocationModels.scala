package whereis.services

import spray.json._
import DefaultJsonProtocol._

object LocationModels {
  // use it for anything to be used as a point on a map, eg bus stop etc
  case class Point(id: String, mref: String, pointType: String, lat: Double, lon: Double, notes: String)
  case class Points(points: Seq[Point])
}


object LocationModelsJsonProtocol extends DefaultJsonProtocol {
  import LocationModels._
  
  implicit val PointJsonFormat = jsonFormat6(Point)
  implicit val PointsJsonFormat = jsonFormat1(Points)
}

