package roflsoft.model

import java.util.UUID

import doobie.quill.DoobieContext
import io.roflsoft.db.PostgresModel
import roflsoft.model.enumeration.Country
import io.getquill.{ Literal, idiom => _ }

case class User(username: String, hashedPassword: String, nationality: Country, id: Long) extends PostgresModel
