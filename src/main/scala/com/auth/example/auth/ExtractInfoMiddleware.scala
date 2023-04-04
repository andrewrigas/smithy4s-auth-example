package com.auth.example.auth

import cats.data.OptionT
import cats.syntax.all._
import com.auth.example.domain.User
import org.http4s.headers.`Authorization`
import org.http4s.{BasicCredentials, HttpRoutes}
import zio._
import zio.interop.catz._

object ExtractInfoMiddleware {

  def withRequestInfo(
      routes: HttpRoutes[Task],
      local: FiberRef[Option[User]]
  ): HttpRoutes[Task] =
    HttpRoutes[Task] { request =>
      val maybeUserRequest = request.headers
        .get[`Authorization`]
        .collect { case Authorization(BasicCredentials(email, password)) => User(email, password) }

      val set: Task[Unit] = local.set(maybeUserRequest)

      OptionT.liftF(set) *> routes(request)
    }
}
