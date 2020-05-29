package roflsoft

import io.getquill.{ PostgresMonixJdbcContext, SnakeCase }
import io.getquill.context.monix.Runner
import io.roflsoft.db.MonadTransactor
import io.roflsoft.db.transactorStore.fromDriverMananger
import monix.eval.Task
import monix.execution.Scheduler

package object database {

  implicit val asyncDatabaseTransactor: MonadTransactor[Task] =
    fromDriverMananger[Task]("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/Nigiri", "admin", "admin")

  //  lazy val context = new PostgresMonixJdbcContext(SnakeCase, "ctx", Runner.using(Scheduler.io()))
}
