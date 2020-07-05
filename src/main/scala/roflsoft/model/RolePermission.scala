package roflsoft.model

import io.roflsoft.db.PostgresModel
import io.getquill.{ idiom => _ }

case class RolePermission(roleId: Long, permissionId: Long, id: Long) extends PostgresModel
