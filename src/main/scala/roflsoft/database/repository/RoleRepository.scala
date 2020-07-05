package roflsoft.database.repository

import com.google.inject.Inject
import doobie._
import fs2._
import roflsoft.database.Repository
import roflsoft.model.{ Permission, Role, RolePermission, User }

class RoleRepository @Inject() () extends Repository[Role] {
  import ctx._

  def create(role: Role): ConnectionIO[Role] = run {
    quote {
      query[Role].insert(lift(role)).returning(id => id)
    }
  }

  def findByIds(ids: Long*): Stream[ConnectionIO, Role] = stream {
    query[Role]
      .filter(role => lift(ids).contains(role.id))
  }

  def findPermissions(roleId: Long): Stream[ConnectionIO, Permission] = stream {
    quote {
      query[RolePermission]
        .filter(rolePermission => rolePermission.roleId == lift(roleId))
        .join(query[Permission])
        .on(_.permissionId == _.id)
        .map(_._2)
    }
  }
}
