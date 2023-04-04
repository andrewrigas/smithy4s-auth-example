package com.auth.example.repository

import com.auth.example.domain.User
import zio.{Task, ZIO}

trait UserManagementRepository {
  def getUser(email: String): Task[Option[User]]
}

object UserManagementRepository extends UserManagementRepository {

  val database = Map("some@email.com" -> User("some@email.com", "password"))

  override def getUser(email: String): Task[Option[User]] = ZIO.succeed(database.get(email))
}
