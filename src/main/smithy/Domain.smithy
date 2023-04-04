$version: "2.0"

namespace com.auth.example.http

structure UserRegistrationForm {
    @required
    email: String
    @required
    password: String
}
