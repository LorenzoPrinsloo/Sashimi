package roflsoft.model

import io.roflsoft.db.PostgresModel
import roflsoft.model.enumeration.Country
import io.getquill.{ idiom => _ }

case class User(username: String, hashedPassword: String, nationality: Country, id: Long) extends PostgresModel {

}
