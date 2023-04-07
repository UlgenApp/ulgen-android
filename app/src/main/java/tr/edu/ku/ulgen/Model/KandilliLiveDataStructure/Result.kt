package tr.edu.ku.ulgen.Model.KandilliLiveDataStructure

data class Result(
    val _id: String,
    val created_at: Int,
    val date: String,
    val date_time: String,
    val depth: Double,
    val earthquake_id: String,
    val geojson: Geojson,
    val location_properties: LocationProperties,
    val location_tz: String,
    val mag: Double,
    val provider: String,
    val rev: Any,
    val title: String
)