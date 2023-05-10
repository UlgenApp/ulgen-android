package tr.edu.ku.ulgen.model.heatmapdatastructure

data class HeatMapResponse(
    val headers: Headers,
    val body: Body,
    val statusCode: String,
    val statusCodeValue: Int
)

data class Headers(
    val `content-length`: List<String>,
    val `content-type`: List<String>,
    val date: List<String>,
    val server: List<String>
)

data class Body(
    val result: Result
)

data class Result(
    val centroids: List<Centroid>
)

data class Centroid(
    val priority: Int,
    val latitude: Double,
    val longitude: Double
)