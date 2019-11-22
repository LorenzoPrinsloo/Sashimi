package roflsoft.models.enums

import io.roflsoft.enums.{Entry, Enum}

sealed abstract class Country(val entry: String) extends Entry(entry)
case object Country extends Enum[Country] {
  case object ZA extends Country("South Africa")
  case object ENG extends Country("England")
  case object USA extends Country("United States of America")

  val values = findValues
}
