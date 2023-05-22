package tr.edu.ku.ulgen.model.apibodies

import com.google.gson.annotations.SerializedName

data class AdditionalInfoBody(
    @SerializedName("additionalInfo")
    val additionalInfo: String
)
