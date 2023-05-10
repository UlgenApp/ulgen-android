package tr.edu.ku.ulgen.model.routingmapdatastructure

data class RoutingMapResponse(
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
    val centroids: List<Centroid>,
    val route: Map<String, Vehicle>
)

data class Centroid(
    val priority: Int,
    val latitude: Double,
    val longitude: Double
)

data class Vehicle(
    val route: List<Int>,
    val distance_travelled: Int
)
