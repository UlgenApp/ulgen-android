package tr.edu.ku.ulgen.model.apibodies

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val email: String,
    val additionalInfo: String?
)