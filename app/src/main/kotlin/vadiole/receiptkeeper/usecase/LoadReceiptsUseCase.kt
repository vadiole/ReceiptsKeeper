package vadiole.receiptkeeper.usecase

import kotlinx.coroutines.flow.MutableStateFlow
import vadiole.receiptkeeper.model.domain.HistoryDomain
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class LoadReceiptsUseCase @Inject constructor() {
    private val now: LocalDateTime = LocalDateTime.now()
    fun getReceipts() = MutableStateFlow( // fake data for now
        List(2000) {
            val randomRaw = UUID.randomUUID().toString()
            val title = randomRaw.take(12)
            val date = now.minusHours(it * 7L)
            HistoryDomain.Receipt(it, title, date, randomRaw)
        }
    )
}
