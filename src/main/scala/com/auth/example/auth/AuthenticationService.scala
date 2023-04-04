package com.auth.example.auth

import com.auth.example.domain.User
import com.auth.example.repository.UserManagementRepository
import zio.{Task, ZIO}

trait AuthenticationService {

  def authenticate(user: User): Task[Unit]
}

final class AuthenticationServiceImpl(userManagementRepository: UserManagementRepository)
    extends AuthenticationService {

  override def authenticate(user: User): Task[Unit] =
    for {
      maybeUserRepo <- userManagementRepository.getUser(user.email)
      userRepo      <- ZIO.fromOption(maybeUserRepo).mapError(_ => new Exception("User not found"))
      _ <- ZIO
        .ifZIO(ZIO.succeed(userRepo.password == user.password))(ZIO.unit, ZIO.fail(new Exception("User not verified")))
    } yield ()
}
