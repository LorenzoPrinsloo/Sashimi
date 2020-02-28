package roflsoft.service.impl

import scala.concurrent.duration._
import akka.http.scaladsl.model.StatusCodes
import cats.data.{ EitherT, Nested }
import com.google.inject.Inject
import io.roflsoft.validation.FormValidator
import io.roflsoft.db.{ complete, completeStream }
import io.roflsoft.db.transactorStore.taskTransactor
import io.roflsoft.db.transactorStore.taskStreamListConverter
import monix.execution.Scheduler.Implicits.global
import monix.eval.Task
import roflsoft.model.request.{ UserLoginRequest, UserRegisterRequest }
import roflsoft.service.api.UserService
import io.roflsoft.db.PostgresRepository
import roflsoft.model.User
import roflsoft.model.enumeration.Country
import roflsoft.model.response.common.ErrorPayload
import roflsoft.model.response.UserLoginResponse
import cats.implicits._
import com.github.t3hnar.bcrypt._
import doobie.implicits._
import io.roflsoft.http.authentication.{ AuthToken, Session }
import doobie._
import roflsoft.database.UserRepository

import scala.language.postfixOps
import scala.languageFeature.postfixOps

class UserServiceImpl @Inject() (
  registerValidator: FormValidator[UserRegisterRequest],
  userRepo: UserRepository)
  extends UserService[Task] {

  private def validateRegisterRequest(request: UserRegisterRequest): EitherT[Task, ErrorPayload, UserRegisterRequest] =
    EitherT.fromEither[Task](registerValidator.validateForm(request).toEither.leftMap(ErrorPayload.apply))

  private def createUser(request: UserRegisterRequest): EitherT[ConnectionIO, ErrorPayload, User] = {
    request.password.bcryptSafe(2).toEither.map { hashedPassword =>
      User(request.email, hashedPassword, Country.ZA, 1L)
    } match {
      case Left(value) => EitherT.fromEither[ConnectionIO](ErrorPayload.apply(value).asLeft)
      case Right(user) => EitherT.liftF(userRepo.add(user).map(_ => user))
    }
  }

  def register(request: UserRegisterRequest): EitherT[Task, ErrorPayload, User] = {
    for {
      validatedRequest <- validateRegisterRequest(request)
      user <- EitherT(complete(createUser(validatedRequest).value))
    } yield user
  }

  private def findUser(emailAddress: String): EitherT[Task, ErrorPayload, User] = EitherT {
    completeStream(userRepo.findByEmail(emailAddress))
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

  private def invalidateUserSessions(user: User): EitherT[Task, ErrorPayload, List[Session]] = {
    ???
  }

  private def createSession(authToken: AuthToken, user: User): EitherT[Task, ErrorPayload, Session] = {
    ???
  }

  def login(request: UserLoginRequest): EitherT[Task, ErrorPayload, UserLoginResponse] = {
    for {
      user <- findUser(request.emailAddress)
      authToken <- validatePassword(request.password, user)
      _ <- invalidateUserSessions(user)
      session <- createSession(authToken, user)
    } yield UserLoginResponse(session)
  }
}
