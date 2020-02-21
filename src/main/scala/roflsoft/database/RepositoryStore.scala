package roflsoft.database

import com.google.inject.Inject
import doobie.util.Read
import io.roflsoft.db.PostgresRepository
import monix.eval.Task
import roflsoft.model.User
import io.roflsoft.db.transactorStore.taskTransactor
import io.roflsoft.db.transactorStore.taskStreamConverter
import io.roflsoft.http.authentication.Session
import monix.execution.Scheduler.Implicits.global

object RepositoryStore {
  class UserRepository @Inject() (implicit read: Read[User]) extends PostgresRepository[User, Task]("Users")
  class SessionRepository @Inject() (implicit read: Read[Session]) extends PostgresRepository[Session, Task]("Sessions")
}
