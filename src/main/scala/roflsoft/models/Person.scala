package roflsoft.models

import roflsoft.models.enums.Country

case class Person(id: Long, name: String, age: Int, nationality: Country)
