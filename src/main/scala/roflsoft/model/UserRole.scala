package roflsoft.model

import io.roflsoft.db.PostgresModel

case class UserRole(userId: Long, roleId: Long, id: Long) extends PostgresModel
