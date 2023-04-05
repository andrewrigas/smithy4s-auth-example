package com.auth.example.domain

final case class Credentials(email: String, password: String)

final case class DBUser(email: String, password: String)

final case class AuthedUser(email: String, password: String)
