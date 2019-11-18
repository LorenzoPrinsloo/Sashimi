package roflsoft.database

import io.roflsoft.db.DAO
import roflsoft.models.Person

object PersonDAO extends DAO[Person]("Person")
