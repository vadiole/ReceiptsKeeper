package vadiole.receiptkeeper.usecase

import vadiole.receiptkeeper.mapper.ReceiptEntityMapper
import vadiole.receiptkeeper.repository.ReceiptRepository
import javax.inject.Inject

class LoadReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository,
    private val mapper: ReceiptEntityMapper,
) {
    suspend fun invoke(id: String) = kotlin.runCatching {
        val entity = repository.getReceipt(id)!!
        mapper.map(entity)
    }
}