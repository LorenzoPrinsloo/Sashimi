package roflsoft.model

import io.roflsoft.db.PostgresModel

case class Permission(name: String, id: Long) extends PostgresModel
