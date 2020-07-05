package roflsoft.service.api

import cats.data.EitherT
import roflsoft.model.User
import roflsoft.model.request.{ UserLoginRequest, UserRegisterRequest }
import roflsoft.model.response.{ UserLoginResponse, UserRegisterResponse }
import roflsoft.model.response.common.ErrorPayload

import scala.language.higherKinds

trait UserService[F[_]] {

  def register(request: UserRegisterRequest): EitherT[F, ErrorPayload, UserRegisterResponse]

  def login(request: UserLoginRequest): EitherT[F, ErrorPayload, UserLoginResponse]

}
