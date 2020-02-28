package roflsoft.database

import com.google.inject.Inject
import io.roflsoft.db.Repository
import roflsoft.model.User
import io.roflsoft.db.transactorStore.taskTransactor
import io.roflsoft.db.transactorStore.taskStreamListConverter
import monix.execution.Scheduler.Implicits.global
import io.roflsoft.db.doobieCtx._
import doobie._
import fs2._

class UserRepository @Inject() () {

  def add(user: User): ConnectionIO[Long] = run {
    quote {
      query[User].insert(lift(user))
    }
  }

  def findByEmail(emailAddress: String): Stream[ConnectionIO, User] = stream {
    quote {
      query[User].filter(_.username == lift(emailAddress))
    }
  }
}
