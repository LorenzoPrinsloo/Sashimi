package roflsoft.validator

import io.roflsoft.validation.{ FormValidator, ValidationError }
import roflsoft.model.request.UserRegisterRequest
import cats.implicits._
import com.google.inject.Inject

class UserRegisterValidator @Inject() extends FormValidator[UserRegisterRequest] {

  case object PasswordDoesNotMeetCriteria extends ValidationError {
    def errorMessage: String = "Password must be at least 10 characters long, including an uppercase and a lowercase letter, one number and one special character."
  }

  case object UsernameHasSpecialCharacters extends ValidationError {
    def errorMessage: String = "Invalid email address."
  }

  private def validatedUsername(email: String): ValidationResult[String] =
    if (email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) email.validNec
    else UsernameHasSpecialCharacters.invalidNec

  private def validatedPassword(password: String): ValidationResult[String] =
    if (password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) password.validNec
    else PasswordDoesNotMeetCriteria.invalidNec

  override def validateForm(request: UserRegisterRequest): ValidationResult[UserRegisterRequest] =
    (validatedUsername(request.email), validatedPassword(request.password))
      .mapN(UserRegisterRequest)
}
