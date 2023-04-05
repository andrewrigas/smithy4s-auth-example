package com.auth.example.auth

import com.auth.example.domain.Credentials
import com.auth.example.repository.UserManagementRepository
import com.auth.example.http._
import zio.{Task, ZIO}
import com.auth.example.domain.AuthedUser

trait AuthenticationService {

  def authenticate(user: Credentials): Task[AuthedUser]
}

final class AuthenticationServiceImpl(userManagementRepository: UserManagementRepository)
    extends AuthenticationService {

  override def authenticate(user: Credentials): Task[AuthedUser] =
    for {
      maybeUserRepo <- userManagementRepository.getUser(user.email)
      userRepo      <- ZIO.fromOption(maybeUserRepo).mapError(_ => NotFound())
      _             <- ZIO.ifZIO(ZIO.succeed(userRepo.password == user.password))(ZIO.unit, ZIO.fail(Unauthorized()))
    } yield AuthedUser(userRepo.email, userRepo.firstName, userRepo.lastName)
}
