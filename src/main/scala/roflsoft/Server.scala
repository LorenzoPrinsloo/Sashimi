package roflsoft

import akka.http.scaladsl.marshalling.GenericMarshallers._
import akka.http.scaladsl.model.{ HttpResponse, ResponseEntity, StatusCodes }
import akka.http.scaladsl.server.Directives.{ as, concat, entity, pathPrefix, _ }
import akka.http.scaladsl.server.{ Route, RouteResult }
import com.google.inject.{ Guice, Injector }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.roflsoft.db.session.ROSessionStore
import net.codingwell.scalaguice.InjectorExtensions._
import io.roflsoft.http.authentication._
import io.roflsoft.http.server.SimpleWebServer
import io.roflsoft.http.task.implicits._
import module.CoreModule
import monix.eval
import monix.eval.Task
import roflsoft.implicits.monix._
import roflsoft.model.request.{ UserLoginRequest, UserRegisterRequest }
import roflsoft.service.api.UserService

import scala.language.postfixOps

object Server extends SimpleWebServer with FailFastCirceSupport {
  val injector: Injector = Guice.createInjector(new CoreModule)
  implicit val sessionStore: ROSessionStore = ???

  lazy val routes: Route = userRoutes

  private val userService: UserService[Task] = injector.instance[UserService[Task]]

  lazy val userRoutes: Route =
    concat(
      pathPrefix("login") {
        post {
          entity(as[UserLoginRequest]) { request =>
            userService.login(request).map(_ => "").value
          }
        }
      },
      pathPrefix("register") {
        authenticateWithPermission("userCreate")(sessionStore) { session =>
          post {
            entity(as[UserRegisterRequest])(request => userService.register(request).value)
          }
        }
      })
}

