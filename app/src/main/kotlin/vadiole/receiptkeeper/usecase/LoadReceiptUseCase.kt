package vadiole.receiptkeeper.usecase

import vadiole.receiptkeeper.mapper.ReceiptEntityMapper
import vadiole.receiptkeeper.model.domain.HistoryDomain
import vadiole.receiptkeeper.repository.ReceiptRepository
import javax.inject.Inject

class LoadReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository,
    private val mapper: ReceiptEntityMapper,
) {
    suspend fun invoke(id: String): HistoryDomain.Receipt? {
        val entity = repository.getReceipt(id) ?: return null
        return mapper.map(entity)
    }
}