package tr.edu.ku.ulgen.model.kandillilivedatastructure

data class KandilliEarthquakeLiveData(
    val desc: String,
    val httpStatus: Int,
    val metadata: Metadata,
    val result: List<Result>,
    val serverloadms: Int,
    val status: Boolean
)