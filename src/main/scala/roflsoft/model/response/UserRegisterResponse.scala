package roflsoft.model.response

import roflsoft.model.User
import roflsoft.model.enumeration.Country

case class UserRegisterResponse(email: String, country: Country, permissions: List[String])
