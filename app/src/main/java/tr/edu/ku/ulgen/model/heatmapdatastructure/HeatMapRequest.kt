package tr.edu.ku.ulgen.model.heatmapdatastructure

data class HeatMapRequest(
    val epsilon: Double,
    val cities: List<String>
)