package tr.edu.ku.ulgen.model.apibodies

data class UserBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)
