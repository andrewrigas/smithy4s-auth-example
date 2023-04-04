$version: "2.0"

namespace com.auth.example.http

use smithy4s.meta#vector

@error("client")
@httpError(400)
structure BadRequest {
    @required
    code: Integer = 400
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
    code: Integer = 404
    @required
    message: String = "Resource not found."
}

@error("client")
@httpError(409)
structure Conflict {
    @required
    code: Integer = 409
    @required
    message: String = "Conflict with the current state of the resource"
}

@error("server")
@httpError(500)
structure InternalServerError {
    @required
    code: Integer = 500
    @required
    message: String = "Internal server error."
}

@error("server")
@httpError(503)
structure ServiceUnavailable {
    @required
    code: Integer = 503
    @required
    message: String = "The server is currently unavailable."
}
