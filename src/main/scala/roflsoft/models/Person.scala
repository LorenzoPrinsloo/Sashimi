package roflsoft.models

import roflsoft.models.enums.Country

case class Person(id: Long, name: String, age: Int, nationality: Country)

object Person {
  case class Patch(id: Option[Long] = None, name: Option[String], age: Option[Int], nationality: Option[Country])
}
