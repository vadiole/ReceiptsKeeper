package vadiole.receiptkeeper.model.raw

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptRaw(
    @SerialName("check")
    val receipt64: String,
    @SerialName("fn")
    val fiscalNumber: String,
    @SerialName("name")
    val name: String,
)