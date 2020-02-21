package roflsoft.model

import java.util.UUID

import io.roflsoft.db.PostgresModel
import roflsoft.model.enumeration.Country

case class User(username: String, hashedPassword: String, nationality: Country, uuid: UUID = UUID.randomUUID()) extends PostgresModel
