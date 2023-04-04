package com.auth.example.service

import com.auth.example.domain.User
import com.auth.example.http.{BadRequest, NotFound, UserManagementService, UserRegistrationForm}
import com.auth.example.repository.UserManagementRepository
import zio.{FiberRef, Task}

final class UserManagementServiceImpl(
    userManagementRepository: UserManagementRepository,
    userFRef: FiberRef[Option[User]]
) extends UserManagementService[Task] {

  override def register(memberRegistrationForm: UserRegistrationForm): Task[Unit] = ???

  // I need the user request data here from Authorization header.
  override def login(): Task[Unit] =
    for {
      user     <- userFRef.get.someOrFail(BadRequest())
      userRepo <- userManagementRepository.getUser(user.email).someOrFail(NotFound())
    } yield ()
}
