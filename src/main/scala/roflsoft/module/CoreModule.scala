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
import roflsoft.database.Repository
import roflsoft.database.repository.{ PermissionRepository, RoleRepository, UserRepository }
import roflsoft.model.{ RolePermission, User, UserRole }
import roflsoft.model.request.UserRegisterRequest
import roflsoft.service.api.UserService
import roflsoft.service.impl.UserServiceImpl

class CoreModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    /** Repositories */
    bind[UserRepository]
    bind[RoleRepository]
    bind[PermissionRepository]

    /** Services */
    bind[UserService[Task]].to[UserServiceImpl]
  }
}
