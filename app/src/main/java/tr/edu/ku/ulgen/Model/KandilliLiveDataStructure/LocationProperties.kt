package tr.edu.ku.ulgen.Model.KandilliLiveDataStructure

data class LocationProperties(
    val airports: List<Airport>,
    val closestCities: List<ClosestCity>,
    val closestCity: ClosestCity,
    val epiCenter: EpiCenter
)