$version: "2.0"

namespace com.auth.example.http

use alloy#simpleRestJson

@simpleRestJson
@httpBasicAuth
service UserManagementService {
    version: "1.0.0"
    operations: [Register, Login]
}

@auth([])
@http(method: "POST", uri: "/register", code: 204)
operation Register {
    input := {
        @required
        memberRegistrationForm: UserRegistrationForm
    }
    errors: [InternalServerError, BadRequest, Conflict]
}

@auth([httpBasicAuth])
@http(method: "POST", uri: "/login", code: 200)
operation Login {
    errors: [InternalServerError, BadRequest, Unauthorized, NotFound]
}