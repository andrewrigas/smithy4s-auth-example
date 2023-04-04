package com.auth.example.auth

import com.auth.example.domain.User
import org.http4s.HttpApp
import smithy4s.Hints
import smithy4s.http4s.ServerEndpointMiddleware
import zio.{FiberRef, Task}

final class AuthMiddleware(authenticationMiddleware: AuthenticationMiddleware)
    extends ServerEndpointMiddleware.Simple[Task] {

  override def prepareWithHints(serviceHints: Hints, endpointHints: Hints): HttpApp[Task] => HttpApp[Task] =
    (serviceHints.get[smithy.api.HttpBasicAuth], serviceHints.get[smithy.api.HttpBearerAuth]) match {
      case (Some(_), None) =>
        endpointHints.get[smithy.api.Auth] match {
          case Some(auths) if auths.value.isEmpty => identity
          case Some(_)                            => authenticationMiddleware.mid()
          case _                                  => identity
        }
      case _ => identity
    }
}
