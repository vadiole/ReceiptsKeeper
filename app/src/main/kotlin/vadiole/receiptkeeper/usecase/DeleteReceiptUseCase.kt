package vadiole.receiptkeeper.usecase

import vadiole.receiptkeeper.repository.ReceiptRepository
import javax.inject.Inject

class DeleteReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository,
) {
    suspend fun invoke(id: String) = repository.deleteReceipt(id)
}