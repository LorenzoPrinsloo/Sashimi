package roflsoft.module

import com.google.inject.AbstractModule
import io.roflsoft.db.PostgresRepository
import io.roflsoft.http.authentication.Session
import io.roflsoft.validation.FormValidator
import monix.eval.Task
import net.codingwell.scalaguice.ScalaModule
import roflsoft.database.RepositoryStore.UserRepository
import roflsoft.model.User
import roflsoft.model.request.UserRegisterRequest
import roflsoft.service.api.UserService
import roflsoft.service.impl.UserServiceImpl
import roflsoft.validator.UserRegisterValidator

class CoreModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    /** Repositories */
    bind[PostgresRepository[User, Task]].to[UserRepository].asEagerSingleton()
    bind[PostgresRepository[Session, Task]].to[UserRepository].asEagerSingleton()

    /** Services */
    bind[UserService[Task]].to[UserServiceImpl].asEagerSingleton()

    /** Form Validators */
    bind[FormValidator[UserRegisterRequest]].to[UserRegisterValidator].asEagerSingleton()
  }
}
