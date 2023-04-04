package com.auth.example.auth

import com.auth.example.domain.User
import com.auth.example.http.{BadRequest, InternalServerError, NotFound, Unauthorized}
import org.http4s.headers.Authorization
import org.http4s.{BasicCredentials, HttpApp}
import zio.interop.catz._
import zio.{FiberRef, Task, ZIO}

trait AuthenticationMiddleware {

  def mid(): HttpApp[Task] => HttpApp[Task]
}

final class AuthenticationMiddlewareImpl(authenticationService: AuthenticationService, userFRef: FiberRef[Option[User]])
    extends AuthenticationMiddleware {

  override def mid(): HttpApp[Task] => HttpApp[Task] = { inputApp =>
    HttpApp[Task] { request =>
      // I was lazy to create an ADT exceptions are fine :)
      userFRef.get
        .someOrFail(new Exception("Bad request"))
        .flatMap(authenticationService.authenticate)
        .mapError {
          case e if e.getMessage.contains("Bad request")       => BadRequest()
          case e if e.getMessage.contains("User not found")    => NotFound()
          case e if e.getMessage.contains("User not verified") => Unauthorized()
          case _                                               => InternalServerError()
        }
        .flatMap(_ => inputApp(request))
    }
  }
}
