package roflsoft.module

import cats.effect.IO
import com.google.inject.AbstractModule
import doobie.quill.DoobieContext
import io.getquill.{ Escape, NamingStrategy }
import io.roflsoft.db.PostgresRepository
import io.roflsoft.http.authentication.Session
import io.roflsoft.validation.FormValidator
import monix.eval.Task
import net.codingwell.scalaguice.ScalaModule
import roflsoft.database.{ Repository, UserRepository }
import roflsoft.model.User
import roflsoft.model.request.UserRegisterRequest
import roflsoft.service.api.UserService
import roflsoft.service.impl.UserServiceImpl

class CoreModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    /** Repositories */
    bind[UserRepository]

    /** Form Validators */
    //    bind[UserRegisterValidator]

    /** Services */
    bind[UserService[Task]].to[UserServiceImpl]

  }
}
