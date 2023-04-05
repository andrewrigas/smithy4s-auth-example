package com.auth.example.service

import com.auth.example.http.{BadRequest, NotFound, UserManagementService, UserRegistrationForm}
import com.auth.example.repository.UserManagementRepository
import zio.{FiberRef, Task}
import com.auth.example.domain.AuthedUser

final class UserManagementServiceImpl(
    userManagementRepository: UserManagementRepository,
    getCurrentUser: Task[AuthedUser]
) extends UserManagementService[Task] {

  override def register(memberRegistrationForm: UserRegistrationForm): Task[Unit] = ???

  // I need the user request data here from Authorization header.
  override def login(): Task[Unit] =
    for {
      user     <- getCurrentUser
      userRepo <- userManagementRepository.getUser(user.email).someOrFail(NotFound())
    } yield ()
}
