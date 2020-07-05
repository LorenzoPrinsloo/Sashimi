package roflsoft.database.repository

import com.google.inject.Inject
import roflsoft.database.Repository
import roflsoft.model.{ Permission, Role }
import doobie._
import fs2._

class PermissionRepository @Inject() () extends Repository[Permission] {
  import ctx._

}
