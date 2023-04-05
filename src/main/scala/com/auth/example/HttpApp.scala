package com.auth.example

import com.auth.example.http._
import com.auth.example.auth.{AuthMiddleware, AuthenticationServiceImpl}
import com.auth.example.repository.UserManagementRepository
import com.auth.example.service.UserManagementServiceImpl
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import smithy4s.http4s.SimpleRestJsonBuilder
import zio.interop.catz._
import zio.{Fiber, FiberRef, Task, ULayer, ZLayer}
import com.auth.example.domain.AuthedUser

object HttpApp {

  val resourceRoutes = FiberRef.make(Option.empty[AuthedUser]).flatMap { userRef =>
    val authenticationService = new AuthenticationServiceImpl(UserManagementRepository)
    val storeCurrentUser      = (user: AuthedUser) => userRef.set(Some(user))
    val getCurrentUser        = userRef.get.someOrFail(BadRequest())
    val authMiddleware        = new AuthMiddleware(authenticationService, storeCurrentUser)
    // val authMiddleware           = new AuthMiddleware(authenticationMiddleware)

    SimpleRestJsonBuilder
      .routes(new UserManagementServiceImpl(UserManagementRepository, getCurrentUser))
      .middleware(authMiddleware)
      .mapErrors(_ => InternalServerError())
      .resource
      .toScopedZIO
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
