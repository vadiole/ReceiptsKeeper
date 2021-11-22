package vadiole.receiptkeeper.model.domain

import android.net.Uri
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


sealed class HistoryDomain {

    class Receipt(val id: String, val title: String, val datetime: LocalDateTime, val plainText: String) :
        HistoryDomain() {
        val date: LocalDate get() = datetime.toLocalDate()
        val url = Uri.parse("https://cabinet.sfs.gov.ua/cashregs/check?id=$id&date=${date.format(formatter)}")
    }

    class Date(val localDate: LocalDate) : HistoryDomain()

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    }
}
