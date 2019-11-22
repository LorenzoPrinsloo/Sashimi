package roflsoft

import akka.http.scaladsl.server.Directives.{as, concat, entity, pathPrefix, _}
import akka.http.scaladsl.server.{RequestContext, Route}
import com.redis.RedisClient
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.{Json, JsonObject}
import io.circe.generic.auto._
import io.roflsoft.db.session.RedisSessionStore
import io.roflsoft.db.transactorStore.taskTransactor
import io.roflsoft.http.authentication._
import io.roflsoft.http.server.SimpleWebServer
import io.roflsoft.http.task.implicits._
import monix.eval.Task
import play.api.libs.json.JsValue
import roflsoft.database.PersonDAO
import roflsoft.implicits.monix._
import roflsoft.models.Person
import spray.json.{JsObject, JsString}

object Server extends SimpleWebServer with FailFastCirceSupport {
  lazy val routes: Route = userRoutes

  val sessionStore = new RedisSessionStore(new RedisClient("localhost", 32776))

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
              PersonDAO.insert(person)
            }
          },
          patch {
            parameters('id.as[Long]) { id =>
              entity(as[Map[String, String]]) { updates =>
                PersonDAO.updateById(id)(updates.toSeq: _*)
              }
            }
          },
          get {
            parameters('id.as[Long]?) { idOption =>
              val result = idOption.map(id => PersonDAO.selectById(id).map(List.apply(_)))
                .getOrElse(PersonDAO.selectAll)

              result
            }
          },
          delete {
            parameters('id.as[Long]) { id =>
              PersonDAO.deleteById(id)
            }
          }
        )
//        }
      }
    )
}

