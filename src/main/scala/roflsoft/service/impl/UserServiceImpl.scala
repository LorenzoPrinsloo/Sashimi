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
import roflsoft.model.User
import roflsoft.model.enumeration.Country
import roflsoft.model.response.common.ErrorPayload
import roflsoft.model.response.UserLoginResponse
import cats.syntax.either._
import com.github.t3hnar.bcrypt._
import doobie.free.connection.ConnectionIO
import doobie.quill.DoobieContext
import io.getquill.Escape
import io.roflsoft.http.authentication.AuthToken
import io.roflsoft.stream.syntax.StreamConverterSyntax.MonixIOStreamConversion
import io.roflsoft.stream.typeclass.StreamConverter.task.taskStreamListConverter
import io.roflsoft.db.conversion._
import roflsoft.database.{ Repository, UserRepository, asyncDatabaseTransactor }
import octopus.syntax._
import scala.language.postfixOps
import scala.languageFeature.postfixOps

class UserServiceImpl @Inject() (userRepo: UserRepository) extends UserService[Task] {

  private def validateRegisterRequest(request: UserRegisterRequest): EitherT[Task, ErrorPayload, UserRegisterRequest] = {
    EitherT.fromEither[Task](request.validate.toEither.leftMap(ErrorPayload.apply))
  }

  private def createUser(request: UserRegisterRequest): EitherT[Task, SQLException, User] = EitherT {
    val user = User(request.email, request.password.bcrypt, Country.ZA, 2L)
    completeSafe(userRepo.create(User(request.email, request.password.bcrypt, Country.ZA, 2L)))
  }

  def register(request: UserRegisterRequest): EitherT[Task, ErrorPayload, User] = {
    (for {
      validatedRequest <- validateRegisterRequest(request)
      user <- createUser(validatedRequest).leftMap(ErrorPayload.apply)
    } yield user)
  }

  private def findUser(emailAddress: String): EitherT[Task, ErrorPayload, User] = EitherT {
    userRepo.findByEmail(emailAddress).runAs[List]
      .map { users =>
        users.headOption.toRight(ErrorPayload("User not found.", StatusCodes.NotFound))
      }
  }

  private def validatePassword(password: String, foundUser: User): EitherT[Task, ErrorPayload, AuthToken] = EitherT.fromEither[Task] {
    for {
      isUser <- password.isBcryptedSafe(foundUser.hashedPassword).toEither.leftMap(ErrorPayload.apply)
      response <- Either.cond(isUser, AuthToken(expires_in = 10 minutes), ErrorPayload("Invalid password for user.", StatusCodes.Unauthorized))
    } yield response
  }

  def login(request: UserLoginRequest): EitherT[Task, ErrorPayload, UserLoginResponse] = {
    for {
      user <- findUser(request.emailAddress)
      authToken <- validatePassword(request.password, user)
    } yield UserLoginResponse(user.id, authToken.access_token)
  }
}
