package tr.edu.ku.ulgen.Model.KandilliLiveDataStructure

data class KandilliEarthquakeLiveData(
    val desc: String,
    val httpStatus: Int,
    val metadata: Metadata,
    val result: List<Result>,
    val serverloadms: Int,
    val status: Boolean
)