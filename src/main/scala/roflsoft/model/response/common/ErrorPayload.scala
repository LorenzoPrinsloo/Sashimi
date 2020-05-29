package roflsoft.model.response.common

import java.sql.SQLException
import akka.http.scaladsl.model.{ StatusCode, StatusCodes }
import cats.implicits._
import octopus.ValidationError

case class ErrorPayload(message: String, code: String, data: List[String] = List.empty[String])
object ErrorPayload {

  def apply(errors: List[ValidationError]): ErrorPayload = {
    new ErrorPayload("Invalid Request.", StatusCodes.BadRequest.defaultMessage, errors.map(_.message))
  }

  def apply(sqlException: SQLException): ErrorPayload = new ErrorPayload(sqlException.getMessage, StatusCodes.InternalServerError.defaultMessage)

  def apply(message: String, code: StatusCode): ErrorPayload = new ErrorPayload(message, code.defaultMessage())

  def apply(throwable: Throwable): ErrorPayload = new ErrorPayload(s"Internal Server Error ${throwable.getLocalizedMessage}.", StatusCodes.InternalServerError.defaultMessage)
}

