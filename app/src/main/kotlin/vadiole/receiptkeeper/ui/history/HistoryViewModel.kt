package vadiole.receiptkeeper.ui.history

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import vadiole.core.base.BaseViewModel
import vadiole.receiptkeeper.model.domain.HistoryDomain
import vadiole.receiptkeeper.model.presentation.HistoryItem
import vadiole.receiptkeeper.usecase.LoadReceiptsUseCase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    loadReceiptsUseCase: LoadReceiptsUseCase,
) : BaseViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    private val dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val today = LocalDate.now()

    val historyData = loadReceiptsUseCase.getReceipts()
        .map { list ->
            val first = list.firstOrNull() ?: return@map emptyList()
            val firstHeader = listOf(HistoryDomain.Date(first.date) as HistoryDomain)
            list.fold(firstHeader) { acc, receipt: HistoryDomain.Receipt ->
                val lastReceipt = acc.last() as? HistoryDomain.Receipt ?: return@fold acc + receipt
                return@fold if (lastReceipt.date != receipt.date) {
                    acc + HistoryDomain.Date(receipt.date) + receipt
                } else {
                    acc + receipt
                }
            }.map { domain ->
                when (domain) {
                    is HistoryDomain.Receipt -> {
                        val datetime = domain.datetime.format(dateTimeFormatter)
                        HistoryItem.Receipt(domain.id, domain.title, datetime)
                    }
                    is HistoryDomain.Date -> {
                        val formatted = domain.localDate.format(dayFormatter)
                        val isToday = domain.localDate == today
                        HistoryItem.Date(formatted, isToday)
                    }
                }
            }
        }
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

}