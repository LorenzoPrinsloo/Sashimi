package roflsoft.service.impl

import java.sql.SQLException

import scala.concurrent.duration._
import akka.http.scaladsl.model.StatusCodes
import cats.data.{ EitherT, Nested }
import com.google.inject.Inject
import io.roflsoft.validation.FormValidator
import monix.execution.Scheduler.Implicits.global
import monix.eval.Task
import roflsoft.model.request.{ UserLoginRequest, UserRegisterRequest }
import roflsoft.service.api.UserService
import roflsoft.model.{ Permission, Role, User, UserRole }
import roflsoft.model.enumeration.Country
import roflsoft.model.response.common.ErrorPayload
import roflsoft.model.response.{ UserLoginResponse, UserRegisterResponse }
import cats.syntax.either._
import com.github.t3hnar.bcrypt._
import doobie.free.connection.ConnectionIO
import doobie.quill.DoobieContext
import io.getquill.Escape
import io.roflsoft.http.authentication.AuthToken
import io.roflsoft.stream.syntax.StreamConverterSyntax.MonixIOStreamConversion
import io.roflsoft.stream.typeclass.StreamConverter.task.taskStreamListConverter
import io.roflsoft.db.conversion._
import roflsoft.database.{ Repository, asyncDatabaseTransactor }
import octopus.syntax._
import roflsoft.database.repository.{ RoleRepository, UserRepository }
import fs2.Stream

import scala.language.postfixOps
import scala.languageFeature.postfixOps
import scala.util.Random

class UserServiceImpl @Inject() (userRepo: UserRepository, roleRepo: RoleRepository) extends UserService[Task] {

  private def validateRegisterRequest(request: UserRegisterRequest): EitherT[Task, ErrorPayload, UserRegisterRequest] = {
    EitherT.fromEither[Task](request.validate.toEither.leftMap(ErrorPayload.apply))
  }

  private def createUserWithRoles(request: UserRegisterRequest): Task[UserRegisterResponse] = {
    completeStream[Permission, Task, List] {
      Stream.eval(userRepo.create(User(request.email, request.password.bcrypt, Country.ZA, new Random().nextInt().toLong)))
        .flatMap { user =>
          userRepo.activateRolesForUser(user.id, request.roleIds: _*)
            .flatMap(userRole => roleRepo.findPermissions(userRole.roleId))
        }
    }.map { permissions =>
      val permissionNames: List[String] = permissions.map(_.name)

      UserRegisterResponse(request.email, Country.ZA, permissionNames)
    }
  }

  def register(request: UserRegisterRequest): EitherT[Task, ErrorPayload, UserRegisterResponse] = {
    (for {
      validatedRequest <- validateRegisterRequest(request)
      user <- EitherT.liftF(createUserWithRoles(validatedRequest))
    } yield user)
  }

  private def findUser(emailAddress: String): EitherT[Task, ErrorPayload, User] = EitherT {
    userRepo.findByEmail(emailAddress).runAs[List]
      .map { users =>
        users.headOption.toRight(ErrorPayload("User not found.", StatusCodes.NotFound))
      }
  }

  private def authenticatePassword(password: String, foundUser: User): EitherT[Task, ErrorPayload, AuthToken] = EitherT.fromEither[Task] {
    for {
      isUser <- password.isBcryptedSafe(foundUser.hashedPassword).toEither.leftMap(ErrorPayload.apply)
      response <- Either.cond(isUser, AuthToken(expires_in = 10 minutes), ErrorPayload("Invalid password for user.", StatusCodes.Unauthorized))
    } yield response
  }

  def login(request: UserLoginRequest): EitherT[Task, ErrorPayload, UserLoginResponse] = {
    for {
      user <- findUser(request.emailAddress)
      authToken <- authenticatePassword(request.password, user)
    } yield UserLoginResponse(user.id, authToken.access_token)
  }
}
