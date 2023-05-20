package tr.edu.ku.ulgen.model.macscannerdatastructure

data class MACScannerRequest(
    val location: Location,
    val macAddresses: List<String>,
    val userCity: String
)

data class Location(
    val latitude: Double,
    val longitude: Double
)