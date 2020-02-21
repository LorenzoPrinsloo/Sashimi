package roflsoft.service.impl

import scala.concurrent.duration._
import akka.http.scaladsl.model.StatusCodes
import cats.data.EitherT
import com.google.inject.Inject
import io.roflsoft.validation.FormValidator
import monix.eval.Task
import roflsoft.model.request.{UserLoginRequest, UserRegisterRequest}
import roflsoft.service.api.UserService
import io.roflsoft.db.PostgresRepository
import roflsoft.model.User
import roflsoft.model.enumeration.Country
import roflsoft.model.response.common.ErrorPayload
import roflsoft.model.response.UserLoginResponse
import cats.implicits._
import com.github.t3hnar.bcrypt._
import doobie.implicits._
import io.roflsoft.http.authentication.{AuthCredentials, AuthToken, Session}
import org.joda.time.LocalDateTime
import scala.language.postfixOps
import scala.languageFeature.postfixOps

class UserServiceImpl @Inject() (
  registerValidator: FormValidator[UserRegisterRequest],
  userRepo: PostgresRepository[User, Task],
  sessionRepo: PostgresRepository[Session, Task])
  extends UserService[Task] {

  private def validateRegisterRequest(request: UserRegisterRequest): EitherT[Task, ErrorPayload, UserRegisterRequest] =
    EitherT.fromEither[Task](registerValidator.validateForm(request).toEither.leftMap(ErrorPayload.apply))

  private def createUser(request: UserRegisterRequest): EitherT[Task, ErrorPayload, User] = EitherT {
    request.password.bcryptSafe(2).toEither.map { hashedPassword =>
      User(request.email, hashedPassword, Country.ZA)
    } match {
      case Left(value) => Task.now(Left(ErrorPayload.apply(value)))
      case Right(user) => userRepo.add(user).attempt.map(_.leftMap(ErrorPayload.apply))
    }
  }

  def register(request: UserRegisterRequest): EitherT[Task, ErrorPayload, User] = {
    for {
      validatedRequest <- validateRegisterRequest(request)
      user             <- createUser(validatedRequest)
    } yield user
  }

  private def findUser(emailAddress: String): EitherT[Task, ErrorPayload, User] = {
    for {
      users     <- userRepo.query(fr"where emailAddress = $emailAddress").attemptT.leftMap(ErrorPayload.apply)
      firstUser <- EitherT.fromEither[Task](users.headOption.toRight(ErrorPayload("User not found.", StatusCodes.NotFound)))
    } yield firstUser
  }

  private def validatePassword(password: String, foundUser: User): EitherT[Task, ErrorPayload, AuthToken] = EitherT.fromEither[Task] {
    for {
      isUser   <- password.isBcryptedSafe(foundUser.hashedPassword).toEither.leftMap(ErrorPayload.apply)
      response <- Either.cond(isUser, AuthToken(expires_in = 10 minutes), ErrorPayload("Invalid password for user.", StatusCodes.Unauthorized))
    } yield response
  }

  private def invalidateUserSessions(user: User): EitherT[Task, ErrorPayload, List[Session]] = {
    sessionRepo.query(fr"where userUUID = ${user.uuid.toString}")
      .flatMap { previousSession =>
        Task.gatherUnordered(
          previousSession.map(session => sessionRepo.update(session.copy(invalidatedAt = Some(LocalDateTime.now()))))
        )
      }
    .attemptT.leftMap(ErrorPayload.apply)
  }

  private def createSession(authToken: AuthToken, user: User): EitherT[Task, ErrorPayload, Session] = {
    sessionRepo.add(Session(AuthCredentials(user.username, user.hashedPassword), authToken))
      .attemptT.leftMap(ErrorPayload.apply)
  }

  def login(request: UserLoginRequest): EitherT[Task, ErrorPayload, UserLoginResponse] = {
    for {
      user      <- findUser(request.emailAddress)
      authToken <- validatePassword(request.password, user)
      _         <- invalidateUserSessions(user)
      session   <- createSession(authToken, user)
    } yield UserLoginResponse(session)
  }
}