package roflsoft.database.repository

import com.google.inject.Inject
import doobie._
import fs2._
import roflsoft.database.Repository
import roflsoft.model.{ Role, User, UserRole }

import scala.util.Random

class UserRepository @Inject() () extends Repository[User] {
  import ctx._

  def create(user: User): ConnectionIO[User] = run {
    quote {
      query[User].insert(lift(user)).returning(id => id)
    }
  }

  def findByEmail(emailAddress: String): Stream[ConnectionIO, User] = stream {
    quote {
      query[User].filter(_.username == lift(emailAddress))
    }
  }

  def activateRolesForUser(userId: Long, roleIds: Long*): Stream[ConnectionIO, UserRole] = {
    lazy val roles: Stream[ConnectionIO, Role] = stream {
      query[Role]
        .filter(role => lift(roleIds).contains(role.id))
    }

    roles
      .evalMap(role => activateRoleForUser(userId, role.id))
  }

  def activateRoleForUser(userId: Long, roleId: Long): ConnectionIO[UserRole] = {
    run {
      quote {
        query[UserRole]
          .insert(lift(UserRole(userId, roleId, new Random().nextLong())))
          .returning(i => i)
      }
    }
  }
}
