package tr.edu.ku.ulgen.model.kandillilivedatastructure

data class LocationProperties(
    val airports: List<Airport>,
    val closestCities: List<ClosestCity>,
    val closestCity: ClosestCity,
    val epiCenter: EpiCenter
)