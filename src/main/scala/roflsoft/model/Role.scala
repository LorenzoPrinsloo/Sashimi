package roflsoft.model

import io.roflsoft.db.PostgresModel
import io.getquill.{ idiom => _ }

case class Role(roleName: String, id: Long) extends PostgresModel
