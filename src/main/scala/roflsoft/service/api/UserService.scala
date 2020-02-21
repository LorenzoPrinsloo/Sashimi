package roflsoft.service.api

import cats.data.{EitherT, NonEmptyChain}
import roflsoft.model.User
import roflsoft.model.request.{UserLoginRequest, UserRegisterRequest}
import roflsoft.model.response.UserLoginResponse
import roflsoft.model.response.common.ErrorPayload

trait UserService[F[_]] {

  def register(request: UserRegisterRequest): EitherT[F, ErrorPayload, User]

  def login(request: UserLoginRequest): EitherT[F, ErrorPayload, UserLoginResponse]

}
