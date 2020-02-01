package roflsoft

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, Materializer }
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService

package object implicits {

  object akka {
    implicit val system: ActorSystem = ActorSystem("Service")
    implicit val mat: Materializer = ActorMaterializer.create(system)
  }

  object monix {
    implicit lazy val scheduler: SchedulerService = Scheduler.fixedPool(name = "fixed", poolSize = 10)
  }
}
