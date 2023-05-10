package tr.edu.ku.ulgen.model.routingmapdatastructure

data class RoutingMapRequest(
    val epsilon: Double,
    val priority_coefficient: Double,
    val distance_coefficient: Double,
    val vehicleCount: Int,
    val depot: Depot,
    val cities: List<String>
)

data class Depot(val latitude: Double, val longitude: Double)