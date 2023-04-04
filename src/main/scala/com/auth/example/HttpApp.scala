package com.auth.example

import com.auth.example.auth.{
  AuthMiddleware,
  AuthenticationMiddlewareImpl,
  AuthenticationServiceImpl,
  ExtractInfoMiddleware
}
import com.auth.example.domain.User
import com.auth.example.repository.UserManagementRepository
import com.auth.example.service.UserManagementServiceImpl
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import smithy4s.http4s.SimpleRestJsonBuilder
import zio.interop.catz._
import zio.{Fiber, FiberRef, Task, ULayer, ZLayer}

object HttpApp {

  val resourceRoutes = FiberRef.make(Option.empty[User]).flatMap { userFRef =>
    val authenticationService    = new AuthenticationServiceImpl(UserManagementRepository)
    val authenticationMiddleware = new AuthenticationMiddlewareImpl(authenticationService, userFRef)
    val authMiddleware           = new AuthMiddleware(authenticationMiddleware)

    SimpleRestJsonBuilder
      .routes(new UserManagementServiceImpl(UserManagementRepository, userFRef))
      .middleware(authMiddleware)
      .resource
      .toScopedZIO
      .map(routes => ExtractInfoMiddleware.withRequestInfo(routes, userFRef))
  }

  private val server = resourceRoutes.flatMap(routes =>
    EmberServerBuilder
      .default[Task]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .build
      .toScopedZIO)

  val serverLayer: ULayer[Fiber.Runtime[Throwable, Server]] = ZLayer.scoped(server.forkScoped)

}
