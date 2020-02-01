package roflsoft.database

import com.google.inject.{Inject, Injector}
import io.roflsoft.db.DAO
import roflsoft.models.Person

class PersonDAO @Inject() extends DAO[Person, Person.Patch]("Person")
