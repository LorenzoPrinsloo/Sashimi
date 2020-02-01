package roflsoft

import akka.http.scaladsl.server.Directives.{as, concat, entity, pathPrefix, _}
import akka.http.scaladsl.server.Route
import com.google.inject.{Guice, Injector}
import com.redis.RedisClient
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.roflsoft.db.session.RedisSessionStore
import io.roflsoft.db.transactorStore.taskTransactor
import net.codingwell.scalaguice.InjectorExtensions._
import io.roflsoft.http.authentication._
import io.roflsoft.http.server.SimpleWebServer
import io.roflsoft.http.task.implicits._
import modules.CoreModule
import monix.eval.Task
import roflsoft.database.PersonDAO
import roflsoft.implicits.monix._
import roflsoft.models.Person

import scala.language.postfixOps

object Server extends SimpleWebServer with FailFastCirceSupport {
  val injector: Injector = Guice.createInjector(new CoreModule)
  val sessionStore = new RedisSessionStore(new RedisClient("localhost", 32776))

  lazy val routes: Route = userRoutes

  private val personDAO: PersonDAO = injector.instance[PersonDAO]

  lazy val userRoutes: Route =
    concat(
      pathPrefix("login") {
        post {
          entity(as[AuthCredentials]) { creds =>
            Task {
              val userSession = Session(creds, AuthToken())
              sessionStore.set(userSession.token.access_token, Session.unapply(userSession))
              userSession.token
            }
          }
        }
      },
      pathPrefix("person") {
        //        authenticate(sessionStore) { session =>
        concat(
          post {
            entity(as[Person]) { person =>
              personDAO.insert(person)
            }
          },
          patch {
            parameters(Symbol("id").as[Long]) { id =>
              entity(as[Person.Patch]) { update =>
                personDAO.updateById(id)(update)
              }
            }
          },
          get {
            parameters(Symbol("id").as[Long]?) { idOption =>
              val result = idOption.map(id => personDAO.selectById(id).map(List.apply(_)))
                .getOrElse(personDAO.selectAll)

              result
            }
          },
          delete {
            parameters(Symbol("id").as[Long]) { id =>
              personDAO.deleteById(id)
            }
          })
        //        }
      })
}

