$version: "2.0"

namespace com.auth.example.http

use smithy4s.meta#vector

@error("client")
@httpError(400)
structure BadRequest {
    @required
    message: String = "Bad Request."
}

@error("client")
@httpError(401)
structure Unauthorized {
    @required
    code: Integer = 401
    @required
    message: String = "Unauthorized connection."
}

@error("client")
@httpError(404)
structure NotFound {
    @required
    message: String = "Resource not found."
}

@error("client")
@httpError(409)
structure Conflict {
    @required
    message: String = "Conflict with the current state of the resource"
}

@error("server")
@httpError(500)
structure InternalServerError {
    @required
    message: String = "Internal server error."
}

@error("server")
@httpError(503)
structure ServiceUnavailable {
    @required
    message: String = "The server is currently unavailable."
}
