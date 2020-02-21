package roflsoft.model.response.common

import akka.http.scaladsl.model.{ StatusCode, StatusCodes }
import cats.data.NonEmptyChain
import io.roflsoft.validation.ValidationError

case class ErrorPayload(message: String, code: StatusCode, data: Option[NonEmptyChain[Exception]] = None) extends Exception
object ErrorPayload {

  def apply(nonEmptyChain: NonEmptyChain[ValidationError]): ErrorPayload = {
    new ErrorPayload("Invalid Request.", StatusCodes.BadRequest, Some(nonEmptyChain))
  }

  def apply(throwable: Throwable): ErrorPayload = new ErrorPayload(s"Internal Server Error ${throwable.getLocalizedMessage}.", StatusCodes.InternalServerError)
}

