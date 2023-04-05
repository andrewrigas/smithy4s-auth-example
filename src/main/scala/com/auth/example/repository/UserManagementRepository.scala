package com.auth.example.repository

import com.auth.example.domain.DBUser
import zio.{Task, ZIO}

trait UserManagementRepository {
  def getUser(email: String): Task[Option[DBUser]]
}

object UserManagementRepository extends UserManagementRepository {

  val database = Map("some@email.com" -> DBUser("some@email.com", "password"))

  override def getUser(email: String): Task[Option[DBUser]] = ZIO.succeed(database.get(email))
}
