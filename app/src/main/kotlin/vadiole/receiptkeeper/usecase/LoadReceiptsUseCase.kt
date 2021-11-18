package vadiole.receiptkeeper.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vadiole.receiptkeeper.mapper.ReceiptMapper
import vadiole.receiptkeeper.model.domain.HistoryDomain
import vadiole.receiptkeeper.repository.ReceiptRepository
import javax.inject.Inject

class LoadReceiptsUseCase @Inject constructor(
    private val repository: ReceiptRepository,
    private val mapper: ReceiptMapper,
) {
    fun invoke(): Flow<List<HistoryDomain.Receipt>> = repository.getReceipts().map { list ->
        list.map(mapper::map)
    }
}