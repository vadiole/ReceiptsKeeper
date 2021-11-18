package vadiole.receiptkeeper.data.local.converters

import java.time.LocalDateTime

class LocalDateTimeConverter : BaseConverter<LocalDateTime>() {
    override fun parseFromString(value: String): LocalDateTime? = try {
        LocalDateTime.parse(value)
    } catch (e: Exception) {
        null
    }
}