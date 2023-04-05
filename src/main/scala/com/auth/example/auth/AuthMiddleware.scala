package com.auth.example.auth

import com.auth.example.http._
import com.auth.example.domain.Credentials
import org.http4s.HttpApp
import smithy4s.Hints
import smithy4s.http4s.ServerEndpointMiddleware
import zio.{FiberRef, Task}
import zio.interop.catz._
import org.http4s.headers.Authorization
import org.http4s.BasicCredentials
import cats.syntax.all._
import org.http4s.dsl.request
import com.auth.example.domain.AuthedUser

final class AuthMiddleware(authService: AuthenticationService, store: AuthedUser => Task[Unit])
    extends ServerEndpointMiddleware.Simple[Task] {

  override def prepareWithHints(serviceHints: Hints, endpointHints: Hints): HttpApp[Task] => HttpApp[Task] =
    (serviceHints.get[smithy.api.HttpBasicAuth], serviceHints.get[smithy.api.HttpBearerAuth]) match {
      case (Some(_), None) =>
        endpointHints.get[smithy.api.Auth] match {
          case Some(auths) if auths.value.isEmpty => identity
          case Some(_)                            => authMiddleware(_)
          case _                                  => identity
        }
      case _ => identity
    }

  def authMiddleware(underlying: HttpApp[Task]): HttpApp[Task] = HttpApp[Task] { request =>
    val maybeUserRequest = request.headers
      .get[`Authorization`]
      .collect { case Authorization(BasicCredentials(email, password)) => Credentials(email, password) }

    maybeUserRequest
      .liftTo[Task](BadRequest())
      .flatMap(authService.authenticate(_))
      .flatMap(store)
      .flatMap(_ => underlying(request))
  }
}
