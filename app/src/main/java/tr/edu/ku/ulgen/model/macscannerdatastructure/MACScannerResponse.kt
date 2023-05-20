package tr.edu.ku.ulgen.model.macscannerdatastructure

data class MACScannerResponse(
    val headers: Headers,
    val body: Body,
    val statusCode: String,
    val statusCodeValue: Int
)

data class Headers(
    val connection: List<String>,
    val contentType: List<String>,
    val date: List<String>,
    val keepAlive: List<String>,
    val transferEncoding: List<String>
)

data class Body(
    val done: Boolean,
    val cancelled: Boolean,
    val completedExceptionally: Boolean,
    val numberOfDependents: Int
)