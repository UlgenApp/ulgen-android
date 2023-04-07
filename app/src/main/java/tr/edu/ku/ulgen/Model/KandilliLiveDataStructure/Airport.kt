package tr.edu.ku.ulgen.Model.KandilliLiveDataStructure

data class Airport(
    val code: String,
    val coordinates: Coordinates,
    val distance: Double,
    val name: String
)