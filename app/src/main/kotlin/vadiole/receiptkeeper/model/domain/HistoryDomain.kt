package vadiole.receiptkeeper.model.domain

import java.time.LocalDate
import java.time.LocalDateTime


sealed class HistoryDomain {
    class Receipt(val id: String, val title: String, val datetime: LocalDateTime, raw: String) : HistoryDomain() {
        val date: LocalDate get() = datetime.toLocalDate()
    }

    class Date(val localDate: LocalDate) : HistoryDomain()
}
