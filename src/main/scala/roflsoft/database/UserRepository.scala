package roflsoft.database

import com.google.inject.Inject
import roflsoft.model.User
import doobie._
import fs2._
import io.getquill._
import cats.implicits._

class UserRepository @Inject() () extends Repository[User] {
  import ctx._

  def create(user: User): ConnectionIO[User] = run {
    quote {
      query[User].insert(lift(user)).returning(user => user)
    }
  }

  def findByEmail(emailAddress: String): Stream[ConnectionIO, User] = stream {
    quote {
      query[User].filter(_.username == lift(emailAddress))
    }
  }
}
