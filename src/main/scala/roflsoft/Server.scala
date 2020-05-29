package roflsoft

import akka.http.scaladsl.marshalling.GenericMarshallers._
import akka.http.scaladsl.server.Directives.{ as, concat, entity, pathPrefix, _ }
import akka.http.scaladsl.server.Route
import com.google.inject.{ Guice, Injector }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import net.codingwell.scalaguice.InjectorExtensions._
import io.roflsoft.http.server.SimpleWebServer
import io.roflsoft.http.task.implicits._
import module.CoreModule
import monix.eval.Task
import roflsoft.implicits.monix._
import roflsoft.model.request.{ UserLoginRequest, UserRegisterRequest }
import roflsoft.service.api.UserService

import scala.language.postfixOps

object Server extends SimpleWebServer with FailFastCirceSupport {
  val injector: Injector = Guice.createInjector(new CoreModule)

  lazy val routes: Route = userRoutes

  private val userService: UserService[Task] = injector.instance[UserService[Task]]

  lazy val userRoutes: Route = Route.seal(
    concat(
      pathPrefix("login") {
        post {
          entity(as[UserLoginRequest]) { request =>
            userService.login(request).value
          }
        }
      },
      pathPrefix("register") {
        post {
          entity(as[UserRegisterRequest])(request => userService.register(request).value)
        }
      }))
}

